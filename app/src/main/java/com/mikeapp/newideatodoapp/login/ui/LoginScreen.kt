package com.mikeapp.newideatodoapp.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.login.viewmodel.LoginViewModel
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel: LoginViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf(uiState.userName) }
    var password by remember { mutableStateOf(uiState.passwordPlaceholder) }
    var rememberMe by remember { mutableStateOf(true) }
    val usernameError = uiState.userNameError
    val passwordError = uiState.passwordError
    LaunchedEffect(Unit) {
        runBlocking {
            viewModel.loadLocalLoginInfo()
        }?.let {
            username = it.userName
            password = it.passwordHash
            viewModel.autoLogin(localUser = it) {
                navController.navigate("todo") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Username TextField
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    viewModel.dismissUserNameError()
                    username = it
                },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = usernameError != null
            )
            usernameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.dismissPasswordError()
                    password = it
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = passwordError != null
            )
            passwordError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }
        }

        // Remember Me Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(
                text = "Remember Me",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Login Button with Loading State
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
        } else {
            Button(
                onClick = {
                    viewModel.login(username, password, rememberMe) {
                        navController.navigate("todo") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Login")
            }
        }

        // Create New Account Link
        TextButton(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Create New Account")
        }

        // OR Separator
        Text(
            text = "OR",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // RTM API Key Option
        Text(
            text = "I am a Remember The Milk user, I want to use RTM api key",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // RTM API Key Link
        TextButton(
            onClick = {
                navController.navigate("rtm_landing")
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("Set it up")
        }
    }
}