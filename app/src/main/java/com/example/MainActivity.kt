package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.data.repository.AuthRepository
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.TimeUtils

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fix the time offset error by setting process and JVM default timezone to Indian Standard Time (IST - Asia/Kolkata)
        TimeUtils.initIndianStandardTime(applicationContext)

        authRepository = AuthRepository.getInstance(applicationContext)

        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                authRepository.checkExistingSession()
                // Auto-sync real IST time in background
                TimeUtils.syncWithNetwork(applicationContext)
            }

            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(authRepository = authRepository)
                }
            }
        }
    }
}
