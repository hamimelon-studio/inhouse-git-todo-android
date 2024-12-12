package com.mikeapp.newideatodoapp.ui.nav

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mikeapp.newideatodoapp.login.ui.LoginScreen
import com.mikeapp.newideatodoapp.login.ui.RegisterScreen
import com.mikeapp.newideatodoapp.login.ui.RtmLandingScreen
import com.mikeapp.newideatodoapp.main.add.AddTaskScreen
import com.mikeapp.newideatodoapp.main.todo.TodoScreen
import com.mikeapp.newideatodoapp.ui.theme.NewIdeaTodoAppTheme

@Composable
fun AppNav() {
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
                composable("todo") { TodoScreen(navController, innerPadding) }
                composable(
                    "add/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getInt("taskId")
                    AddTaskScreen(navController, innerPadding, taskId)
                }
            }
        }
    }
}