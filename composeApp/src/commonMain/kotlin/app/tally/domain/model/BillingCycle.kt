package app.tally.domain.model

/** AGENTS.md §8. Only MONTHLY/YEARLY are surfaced in the Add form — the rest exist so the model doesn't need a migration later. */
public enum class BillingCycle { MONTHLY, YEARLY, WEEKLY, QUARTERLY, CUSTOM }
