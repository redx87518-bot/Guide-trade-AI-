package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.SecondaryButton
import com.guidetradeai.viewmodel.AuthUiState
import com.guidetradeai.viewmodel.AuthViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ProfileScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val authState = authViewModel.uiState.collectAsState().value
    val userId = (authState as? AuthUiState.Authenticated)?.userId ?: "Unknown"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text("User ID: $userId", fontSize = 13.sp)
        Spacer(modifier = Modifier.height(20.dp))
        SecondaryButton(
            text = "Sign Out",
            onClick = {
                authViewModel.signOut()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            },
        )
    }
}
