package com.mikeapp.newideatodoapp.login.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mikeapp.newideatodoapp.ui.theme.NewIdeaTodoAppTheme

@Composable
fun LoginNav() {
    NewIdeaTodoAppTheme {
        val navController = rememberNavController()
        Scaffold { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") { LoginScreen(navController, innerPadding) }
                composable("register") { RegisterScreen(navController, innerPadding) }
                composable("rtm_landing") { RtmLandingScreen(navController, innerPadding) }
            }
        }
    }
}