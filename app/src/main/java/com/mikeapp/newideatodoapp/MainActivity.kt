package com.mikeapp.newideatodoapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mikeapp.newideatodoapp.alarm.AlarmStarter
import com.mikeapp.newideatodoapp.data.repository.GithubOpenApiRepository
import com.mikeapp.newideatodoapp.ui.theme.NewIdeaTodoAppTheme

class MainActivity : ComponentActivity() {
    private val alarmStarter = AlarmStarter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        GithubOpenApiRepository().test()
        setContent {
            NewIdeaTodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        alarmStarter.setAlarm(this@MainActivity, "2024-11-07", "15:07")
    }

    companion object {
        fun intent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NewIdeaTodoAppTheme {
        Greeting("Android")
    }
}