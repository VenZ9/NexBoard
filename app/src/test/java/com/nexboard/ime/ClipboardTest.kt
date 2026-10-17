package com.nexboard.ime

import com.nexboard.ime.data.clipboard.ClipItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardTest {

    @Test
    fun testClipItemCreation() {
        val clip = ClipItem(text = "Hello NexBoard")
        assertEquals("Hello NexBoard", clip.text)
        assertFalse(clip.isPinned)
        assertTrue(clip.id.isNotBlank())
    }

    @Test
    fun testPinningToggle() {
        val clip = ClipItem(text = "Important Pin", isPinned = false)
        val pinned = clip.copy(isPinned = true)
        assertTrue(pinned.isPinned)
    }

    @Test
    fun testSortingPinnedFirst() {
        val item1 = ClipItem(id = "1", text = "Unpinned old", timestamp = 1000, isPinned = false)
        val item2 = ClipItem(id = "2", text = "Unpinned new", timestamp = 2000, isPinned = false)
        val item3 = ClipItem(id = "3", text = "Pinned old", timestamp = 500, isPinned = true)

        val list = listOf(item1, item2, item3)
        val sorted = list.sortedWith(
            compareByDescending<ClipItem> { it.isPinned }
                .thenByDescending { it.timestamp }
        )

        assertEquals("3", sorted[0].id)
        assertEquals("2", sorted[1].id)
        assertEquals("1", sorted[2].id)
    }
}
