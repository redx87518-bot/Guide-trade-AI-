import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

package com.guidetradeai.ui.screens

@Composable
fun LoginScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isSignUp by rememberSaveable { mutableStateOf(false) }
    var fullName by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Authenticated) {
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.LOGIN) { inclusive = true }
            }
        }
    }
    Scaffold(
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GuideTradeColors.BrightPurple, GuideTradeColors.PrimaryPurple),
                        ),
                        shape = CircleShape,
                    ),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "GuideTrade AI", color = GuideTradeColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(text = "AI Market Intelligence", color = GuideTradeColors.TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
            if (isSignUp) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GuideTradeColors.PrimaryPurple, cursorColor = GuideTradeColors.PrimaryPurple),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GuideTradeColors.PrimaryPurple, cursorColor = GuideTradeColors.PrimaryPurple),
                modifier = Modifier.fillMaxWidth(),
            Spacer(modifier = Modifier.height(12.dp))
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = if (isSignUp) "Sign Up" else "Sign In",
                onClick = {
                    if (isSignUp) authViewModel.signUp(email, password, fullName)
                    else authViewModel.signIn(email, password)
                },
            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(text = if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up", color = GuideTradeColors.BrightPurple)
            if (uiState is AuthUiState.Error) {
                Text(text = (uiState as AuthUiState.Error).message, color = GuideTradeColors.Negative, fontSize = 13.sp, textAlign = TextAlign.Center)
}