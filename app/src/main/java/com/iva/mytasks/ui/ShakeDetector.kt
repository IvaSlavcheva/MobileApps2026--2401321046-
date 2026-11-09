package com.iva.mytasks.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.SystemClock
import kotlin.math.sqrt

/** Разпознава разклащане на телефона по данните от акселерометъра. */
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent) {
        if (isShake(event.values[0], event.values[1], event.values[2])) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastShakeTime > MIN_INTERVAL_MS) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    companion object {
        private const val GRAVITY = 9.80665f
        private const val THRESHOLD_G = 2.5f
        private const val MIN_INTERVAL_MS = 1500L

        /** Истина, ако общото ускорение е над прага (в единици g). */
        fun isShake(x: Float, y: Float, z: Float): Boolean = sqrt(x * x + y * y + z * z) / GRAVITY >= THRESHOLD_G
    }
}
