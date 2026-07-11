package com.divarsmartsearch.app.data.filters

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForbiddenKeywordsTest {

    @Test
    fun `detects moshaver`() {
        assertTrue(ForbiddenKeywords.containsForbiddenAgencyKeyword("این ملک از طریق مشاور معرفی شده"))
    }

    @Test
    fun `detects amlak`() {
        assertTrue(ForbiddenKeywords.containsForbiddenAgencyKeyword("دفتر املاک تهران"))
    }

    @Test
    fun `does not falsely flag unrelated text`() {
        assertFalse(ForbiddenKeywords.containsForbiddenAgencyKeyword("فروش بدون واسطه توسط مالک مستقیم"))
    }

    @Test
    fun `empty or null text`() {
        assertFalse(ForbiddenKeywords.containsForbiddenAgencyKeyword(""))
        assertFalse(ForbiddenKeywords.containsForbiddenAgencyKeyword(null))
    }

    @Test
    fun `even negated mention is still excluded`() {
        // Per explicit user request, this is unconditional.
        assertTrue(ForbiddenKeywords.containsForbiddenAgencyKeyword("لطفا مشاورین تماس نگیرند"))
    }
}

class OwnerDetectorTest {

    @Test
    fun `agency keywords increase heuristic score`() {
        val text = "این ملک کد ملکی ۱۲۳ دارد، جهت هماهنگی بازدید با مشاور املاک تماس بگیرید"
        assertTrue(OwnerDetector.heuristicAgencyProbability(text) > 0.7)
    }

    @Test
    fun `owner keywords decrease heuristic score`() {
        val text = "فروش بدون واسطه توسط مالک مستقیم، لطفا مشاورین تماس نگیرند"
        assertTrue(OwnerDetector.heuristicAgencyProbability(text) < 0.2)
    }

    @Test
    fun `empty description is neutral`() {
        assertEquals(0.5, OwnerDetector.heuristicAgencyProbability(null), 0.001)
        assertEquals(0.5, OwnerDetector.heuristicAgencyProbability(""), 0.001)
    }

    @Test
    fun `falls back to heuristic when no api key configured`() = runTest {
        val text = "فروش بدون واسطه توسط مالک مستقیم"
        val result = OwnerDetector.agencyProbability(text, apiKey = null, model = "claude-haiku-4-5-20251001")
        assertEquals(OwnerDetector.heuristicAgencyProbability(text), result, 0.001)
    }
}
