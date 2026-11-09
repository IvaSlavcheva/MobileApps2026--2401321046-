package com.iva.mytasks

import com.iva.mytasks.ui.ShakeDetector
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShakeDetectorTest {

    @Test
    fun phoneAtRest_isNotShake() {
        assertFalse(ShakeDetector.isShake(0f, 0f, 9.8f))
    }

    @Test
    fun strongMovement_isShake() {
        assertTrue(ShakeDetector.isShake(20f, 20f, 20f))
    }
}
