package com.divarsmartsearch.app.data.filters

import com.divarsmartsearch.app.data.local.entity.ListingEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneFilterTest {

    private fun listing(
        title: String = "",
        description: String? = null,
        contactPhone: String? = null,
    ) = ListingEntity(
        savedSearchId = 1,
        divarToken = "tok",
        url = "https://divar.ir/v/x/tok",
        title = title,
        description = description,
        contactPhone = contactPhone,
    )

    @Test
    fun `normalize local format`() {
        assertEquals("09121234567", PhoneFilter.normalizePhone("0912-123-4567"))
    }

    @Test
    fun `normalize country code format`() {
        assertEquals("09121234567", PhoneFilter.normalizePhone("+989121234567"))
    }

    @Test
    fun `is blocked via contact field`() {
        val l = listing(contactPhone = "09121234567")
        assertTrue(PhoneFilter.isBlocked(l, setOf("09121234567")))
    }

    @Test
    fun `is blocked via number embedded in description`() {
        val l = listing(description = "برای هماهنگی با ۰۹۱۲۱۲۳۴۵۶۷ تماس بگیرید")
        assertTrue(PhoneFilter.isBlocked(l, setOf("09121234567")))
    }

    @Test
    fun `not blocked when number not in blocklist`() {
        val l = listing(contactPhone = "09121234567")
        assertFalse(PhoneFilter.isBlocked(l, setOf("09350000000")))
    }

    @Test
    fun `not blocked when blocklist empty`() {
        val l = listing(contactPhone = "09121234567")
        assertFalse(PhoneFilter.isBlocked(l, emptySet()))
    }
}
