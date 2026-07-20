package app.tally.domain.model

/**
 * AGENTS.md §8. Pulled forward from Phase 1.1 — [app.tally.ui.components.TallyMonogramTile]
 * needs the real type. Tint/icon are resolved in the UI layer (ui/components/CategoryStyle.kt),
 * never stored on the enum itself.
 */
public enum class Category { ENTERTAINMENT, MUSIC, PRODUCTIVITY, STORAGE, FITNESS, NEWS, GAMING, OTHER }
