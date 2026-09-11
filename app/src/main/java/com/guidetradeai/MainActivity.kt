package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.GuideTradeNavGraph
import com.guidetradeai.ui.theme.GuideTradeTheme
import com.guidetradeai.viewModel.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuideTradeTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val authViewModel: AuthViewModel = viewModel()
                    val navController = rememberNavController()
                    GuideTradeNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                    )
                }
            }
        }
    }
}
