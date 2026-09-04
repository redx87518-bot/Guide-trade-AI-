package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.ui.navigation.NavGraph
import com.guidetradeai.ui.theme.GuideTradeTheme
import com.guidetradeai.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel by lazy { AuthViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = if (SupabaseClient.client.auth.currentSessionOrNull() != null) {
            com.guidetradeai.ui.navigation.NavRoutes.HOME
        } else {
            com.guidetradeai.ui.navigation.NavRoutes.SPLASH
        }
        setContent {
            GuideTradeTheme(darkTheme = true) {
                NavGraph(
                    startDestination = startDestination,
                    authViewModel = authViewModel,
                )
            }
        }
    }
}
