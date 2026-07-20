package app.tally.data.repository

import app.tally.data.local.SubscriptionDao
import app.tally.data.local.toDomain
import app.tally.data.local.toEntity
import app.tally.domain.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Offline-first: every mutation writes to Room **first**, and the Room
 * [Flow] emission is what updates the UI — never a manual refresh call
 * (AGENTS.md §4). No network call anywhere in this class; sync (Phase 5) is
 * a separate background reconciliation layered on top, never a gate here.
 */
public class SubscriptionRepository(private val dao: SubscriptionDao) {

    public fun observeAll(): Flow<List<Subscription>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    public fun observeById(id: String): Flow<Subscription?> =
        dao.observeById(id).map { it?.toDomain() }

    public suspend fun add(subscription: Subscription) {
        dao.upsert(subscription.copy(updatedAt = Clock.System.now()).toEntity())
    }

    public suspend fun update(subscription: Subscription) {
        dao.upsert(subscription.copy(updatedAt = Clock.System.now()).toEntity())
    }

    /** Tombstones rather than hard-deletes — the sync engine (Phase 5) needs `deletedAt` to propagate the delete. */
    public suspend fun delete(id: String) {
        dao.softDelete(id, Clock.System.now())
    }

    public suspend fun undelete(id: String) {
        val entity = dao.getAllIncludingTombstones().find { it.id == id } ?: return
        dao.upsert(entity.copy(deletedAt = null, updatedAt = Clock.System.now()))
    }

    /** Debug-only dev fixture (see `SeedSubscriptions.kt`) — inserts only if the table is currently empty, never overwrites real data. */
    public suspend fun seedIfEmpty(subscriptions: List<Subscription>) {
        if (dao.getAllIncludingTombstones().isNotEmpty()) return
        subscriptions.forEach { dao.upsert(it.toEntity()) }
    }
}
