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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeTextField
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.ui.components.SecondaryButton
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.AuthUiState

@Composable
fun SignUpScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Authenticated -> {
                navController.navigate(NavRoutes.Home.route) {
                    popUpTo(NavRoutes.Signup.route) { inclusive = true }
                }
            }
            is AuthUiState.Loading -> {}
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Create account", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Start your trading journey", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(24.dp))
        GuideTradeTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "Sign Up",
            onClick = { authViewModel.signUp(email, password, fullName) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryButton(
            text = "Already have an account? Sign In",
            onClick = { navController.popBackStack() },
        )
    }
}
