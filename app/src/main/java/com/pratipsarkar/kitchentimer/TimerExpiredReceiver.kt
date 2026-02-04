package com.pratipsarkar.kitchentimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class TimerExpiredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val player = MediaPlayer.create(context, R.raw.alarm_sound)
        player.start()
    }
}