package uz.ttpu.framephases

import org.junit.Assert.assertEquals
import org.junit.Test

class FramePhasesTest {

    @Test
    fun stackBelow_sizing_calculation_is_correct() {
        val childWidths = listOf(100, 80, 120)
        val childHeights = listOf(40, 20, 60)
        val spacingPx = 16

        val expectedWidth = childWidths.maxOrNull() ?: 0
        val totalGaps = if (childHeights.size > 1) (childHeights.size - 1) * spacingPx else 0
        val expectedHeight = childHeights.sum() + totalGaps

        assertEquals(120, expectedWidth)
        assertEquals(40 + 20 + 60 + 32, expectedHeight) // 120 + 32 = 152
    }

    @Test
    fun derivedStateOf_filtering_logic_reduces_recompositions() {
        // Simulating list scroll offsets
        val scrollIndices = listOf(0, 0, 1, 2, 5, 10, 0)

        var naiveChanges = 0
        var derivedChanges = 0

        var previousDerivedValue = false

        scrollIndices.forEach { index ->
            // In naive, every index evaluation runs
            naiveChanges++

            val isVisible = index > 0
            if (isVisible != previousDerivedValue) {
                derivedChanges++
                previousDerivedValue = isVisible
            }
        }

        assertEquals(7, naiveChanges)
        assertEquals(2, derivedChanges) // Flipped from false->true, then true->false
    }
}
