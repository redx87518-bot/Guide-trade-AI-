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
fun SignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val authUiState by authViewModel.uiState.collectAsState()
    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.VerificationSent -> {
                val emailArg = (authUiState as AuthUiState.VerificationSent).email
                navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.verificationRoute(emailArg)) {
                    popUpTo(com.guidetradeai.ui.navigation.NavRoutes.SIGNUP) { inclusive = true }
                }
            }
            is AuthUiState.Authenticated -> {
                navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.HOME) {
                    popUpTo(0) { inclusive = true }
            else -> {}
        }
    }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
            text = "Create an account to start using Guide Trade AI",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = if (it.isBlank()) "Full name is required" else null
            },
            label = { Text("Full Name") },
            isError = fullNameError != null,
            supportingText = { fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
        Spacer(modifier = Modifier.height(16.dp))
            value = email,
                email = it
                emailError = if (it.isNotBlank() && !it.isEmailValid()) "Invalid email" else null
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = password,
                password = it
                passwordError = if (it.isNotBlank() && !it.isPasswordValid()) "At least 8 characters" else null
            label = { Text("Password") },
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
            value = confirmPassword,
                confirmPassword = it
                confirmPasswordError = if (it.isNotBlank() && it != password) "Passwords do not match" else null
            label = { Text("Confirm Password") },
            isError = confirmPasswordError != null,
            supportingText = { confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
        if (authUiState is AuthUiState.Error) {
            Text(
                text = (authUiState as AuthUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                var valid = true
                if (fullName.isBlank()) {
                    fullNameError = "Full name is required"
                    valid = false
                if (!email.isEmailValid()) {
                    emailError = "Invalid email"
                if (password.length < 8) {
                    passwordError = "At least 8 characters"
                if (confirmPassword != password) {
                    confirmPasswordError = "Passwords do not match"
                if (valid) {
                    authViewModel.signUp(email, password, fullName)
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank() && authUiState !is AuthUiState.Loading,
        ) {
                text = if (authUiState is AuthUiState.Loading) "CREATING ACCOUNT..." else "SIGN UP",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        TextButton(
            onClick = { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.LOGIN) },
            enabled = authUiState !is AuthUiState.Loading,
                text = "Already have an account? Login",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
}