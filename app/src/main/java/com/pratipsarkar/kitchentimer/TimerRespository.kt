package com.pratipsarkar.kitchentimer

import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerRepository(
    private val context: Context
) {

    val Context.timerDataStore by preferencesDataStore("timer_prefs")

    object TimerPrefs {
        val START_TIME = longPreferencesKey("start_time")
        val DURATION = longPreferencesKey("duration")
    }

    fun scheduleAlarm(triggerAtMillis: Long) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TimerAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (canScheduleExactAlarms(alarmManager)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelAlarm() {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, TimerAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    suspend fun saveTimer(startTime: Long, duration: Long) {
        context.timerDataStore.edit {
            it[TimerPrefs.START_TIME] = startTime
            it[TimerPrefs.DURATION] = duration
        }
    }

    suspend fun clearTimer() {
        context.timerDataStore.edit {
            it.remove(TimerPrefs.START_TIME)
            it.remove(TimerPrefs.DURATION)
        }
    }

    fun timerFlow(): Flow<TimerState?> =
        context.timerDataStore.data.map { prefs ->
            val start = prefs[TimerPrefs.START_TIME]
            val duration = prefs[TimerPrefs.DURATION]
            if (start != null && duration != null) {
                TimerState(duration, start)
            } else null
        }

    private fun canScheduleExactAlarms(alarmManager: AlarmManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

}