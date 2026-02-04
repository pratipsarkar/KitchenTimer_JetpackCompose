package com.pratipsarkar.kitchentimer

class TimerEngine {
    fun remainingTime(state: TimerState, now: Long): Long {
        // The full time is the remaining time
        if (state.startTimeMillis == null) {
            return state.durationMillis
        }
        val elapsedTime = now - state.startTimeMillis
        if (elapsedTime > state.durationMillis) {
            return 0
        } else {
            return state.durationMillis - elapsedTime
        }
    }
}