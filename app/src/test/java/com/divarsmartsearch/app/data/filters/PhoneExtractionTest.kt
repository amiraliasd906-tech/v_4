package com.divarsmartsearch.app.data.filters

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneExtractionTest {

    @Test
    fun `extracts plain number`() {
        val text = "برای هماهنگی با شماره 09121234567 تماس بگیرید"
        assertEquals(listOf("09121234567"), PhoneExtraction.extractPhoneNumbers(text))
    }

    @Test
    fun `extracts number with dashes and spaces`() {
        val text = "شماره تماس: 0912-123 4567"
        assertEquals(listOf("09121234567"), PhoneExtraction.extractPhoneNumbers(text))
    }

    @Test
    fun `extracts number with country code`() {
        val text = "تماس با +98 912 123 4567"
        assertEquals(listOf("09121234567"), PhoneExtraction.extractPhoneNumbers(text))
    }

    @Test
    fun `extracts multiple distinct numbers`() {
        val text = "مشاور اول: 09121234567 - مشاور دوم: 09359876543"
        assertEquals(listOf("09121234567", "09359876543"), PhoneExtraction.extractPhoneNumbers(text))
    }

    @Test
    fun `deduplicates same number across fields`() {
        val title = "تماس: 09121234567"
        val description = "همون شماره 09121234567 هست"
        assertEquals(listOf("09121234567"), PhoneExtraction.extractPhoneNumbers(title, description))
    }

    @Test
    fun `no numbers returns empty list`() {
        assertEquals(emptyList<String>(), PhoneExtraction.extractPhoneNumbers("فروش بدون واسطه توسط مالک مستقیم"))
    }

    @Test
    fun `ignores null and blank text`() {
        assertEquals(emptyList<String>(), PhoneExtraction.extractPhoneNumbers(null, "", null))
    }

    @Test
    fun `normalizes mobile number variants`() {
        assertEquals("09121234567", PhoneExtraction.normalizeMobileNumber("+989121234567"))
        assertEquals("09121234567", PhoneExtraction.normalizeMobileNumber("00989121234567"))
        assertEquals("09121234567", PhoneExtraction.normalizeMobileNumber("9121234567"))
        assertEquals("09121234567", PhoneExtraction.normalizeMobileNumber("09121234567"))
    }
}
