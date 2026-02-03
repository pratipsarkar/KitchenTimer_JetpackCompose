package com.pratipsarkar.kitchentimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.pratipsarkar.kitchentimer.ui.theme.KitchenTimerTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<TimerViewModel>()
    private val engine = TimerEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitchenTimerTheme {
                val timerViewModel: TimerViewModel = viewModel
                TimerScreen(timerViewModel)
            }
        }
    }
}