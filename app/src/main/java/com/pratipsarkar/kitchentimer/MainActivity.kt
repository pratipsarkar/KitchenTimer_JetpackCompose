package com.pratipsarkar.kitchentimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.pratipsarkar.kitchentimer.ui.theme.KitchenTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenTimerTheme {
                val timerViewModel = TimerViewModel(
                    engine = TimerEngine(),
                    repository = TimerRepository(LocalContext.current)
                )
                TimerScreen(timerViewModel)
            }
        }
    }
}