package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.GuideTradeNavGraph
import com.guidetradeai.ui.theme.GuideTradeTheme
import com.guidetradeai.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuideTradeTheme {
                MaterialTheme {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    GuideTradeNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                    )
                }
            }
        }
    }
}
