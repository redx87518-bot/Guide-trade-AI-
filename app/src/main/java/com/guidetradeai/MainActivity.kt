package com.guidetradeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.guidetradeai.ui.navigation.GuideTradeNavGraph
import com.guidetradeai.ui.theme.GuideTradeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuideTradeTheme {
                MaterialTheme {
                    val navController = rememberNavController()
                    GuideTradeNavGraph(navController = navController)
                }
            }
        }
    }
}
