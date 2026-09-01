package com.herrhythm.app.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Hardware Abstraction Layer for HerRythm.
 * All UI and business components consume HealthSensorRepository.
 * MockWatchDataProvider implements realistic simulated sensor behavior when physical BLE watch is detached.
 */
interface HealthSensorRepository {
    val liveSnapshot: StateFlow<HealthSnapshot>
    fun setWatchConnected(connected: Boolean)
    fun triggerSimulatedStressEvent()
    fun triggerSimulatedWorkoutMode()
    fun triggerSimulatedRestState()
}

class MockWatchDataProvider : HealthSensorRepository {
    private val scope = CoroutineScope(Dispatchers.Default)

    private var baseHeartRate = 72
    private var baseHrv = 54
    private var baseSpo2 = 98
    private var baseStress = 28
    private var isConnected = true

    private val _liveSnapshot = MutableStateFlow(
        HealthSnapshot(
            heartRate = baseHeartRate,
            hrv = baseHrv,
            spo2 = baseSpo2,
            edaStress = baseStress,
            isWatchConnected = isConnected
        )
    )
    override val liveSnapshot: StateFlow<HealthSnapshot> = _liveSnapshot.asStateFlow()

    init {
        // Continuous realistic physiological trend simulator
        scope.launch {
            while (true) {
                delay(3000) // Update every 3 seconds with subtle natural micro-fluctuations
                if (isConnected) {
                    val hrDelta = Random.nextInt(-3, 4)
                    val currentHr = (baseHeartRate + hrDelta).coerceIn(58, 165)
                    
                    val hrvDelta = Random.nextInt(-2, 3)
                    val currentHrv = (baseHrv + hrvDelta).coerceIn(38, 85)

                    val currentSpo2 = (baseSpo2 + Random.nextInt(-1, 2)).coerceIn(95, 100)
                    val currentStress = (baseStress + Random.nextInt(-2, 3)).coerceIn(12, 92)

                    _liveSnapshot.value = _liveSnapshot.value.copy(
                        heartRate = currentHr,
                        hrv = currentHrv,
                        spo2 = currentSpo2,
                        edaStress = currentStress,
                        steps = _liveSnapshot.value.steps + Random.nextInt(0, 5),
                        caloriesBurned = _liveSnapshot.value.caloriesBurned + if (Random.nextBoolean()) 1 else 0,
                        isWatchConnected = true
                    )
                } else {
                    _liveSnapshot.value = _liveSnapshot.value.copy(isWatchConnected = false)
                }
            }
        }
    }

    override fun setWatchConnected(connected: Boolean) {
        isConnected = connected
        _liveSnapshot.value = _liveSnapshot.value.copy(isWatchConnected = connected)
    }

    override fun triggerSimulatedStressEvent() {
        baseHeartRate = 96
        baseStress = 74
        baseHrv = 38
        _liveSnapshot.value = _liveSnapshot.value.copy(
            heartRate = 96,
            edaStress = 74,
            hrv = 38
        )
    }

    override fun triggerSimulatedWorkoutMode() {
        baseHeartRate = 138
        baseStress = 45
        baseHrv = 42
        _liveSnapshot.value = _liveSnapshot.value.copy(
            heartRate = 138,
            edaStress = 45,
            hrv = 42
        )
    }

    override fun triggerSimulatedRestState() {
        baseHeartRate = 65
        baseStress = 18
        baseHrv = 62
        _liveSnapshot.value = _liveSnapshot.value.copy(
            heartRate = 65,
            edaStress = 18,
            hrv = 62
        )
    }
}
