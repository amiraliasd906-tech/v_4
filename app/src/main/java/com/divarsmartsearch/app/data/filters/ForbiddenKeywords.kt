package com.divarsmartsearch.app.data.filters

/**
 * Per explicit user request: if the description literally contains
 * "مشاور" or "املاک" anywhere, the listing is ALWAYS hidden —
 * unconditionally, no probability, no negation exception, regardless of
 * any other filter setting. Runs before the softer heuristic/LLM scoring.
 */
object ForbiddenKeywords {
    private val FORBIDDEN_AGENCY_KEYWORDS = listOf("مشاور", "املاک")

    fun containsForbiddenAgencyKeyword(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return FORBIDDEN_AGENCY_KEYWORDS.any { text.contains(it) }
    }
}
