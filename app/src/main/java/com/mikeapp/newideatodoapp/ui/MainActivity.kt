package com.mikeapp.newideatodoapp.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mikeapp.newideatodoapp.alarm.AlarmStarter
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.mikeapp.newideatodoapp.map.MapActivity
import com.mikeapp.newideatodoapp.ui.extension.adaptEdgeToEdge
import com.mikeapp.newideatodoapp.ui.nav.AppNav
import com.mikeapp.newideatodoapp.ui.theme.NewIdeaTodoAppTheme
import org.koin.java.KoinJavaComponent.get

class MainActivity : ComponentActivity() {
    private val alarmStarter = AlarmStarter()

    private val geofenceUseCase: GeofenceUseCase = get(GeofenceUseCase::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        adaptEdgeToEdge(window.decorView)
        geofenceUseCase.requestPermissions(this@MainActivity)
        geofenceUseCase.register()
        setContent {
            AppNav()
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this@MainActivity, MapActivity::class.java)
//        activityResultLauncher.launch(intent)

//        SupabaseUseCase().test()
//        SupabaseUseCase().createUser()

//        alarmStarter.setAlarm(this@MainActivity, "2024-11-07", "15:07")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted
            } else {

            }
        }
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            val radius = data?.getDoubleExtra("radius", 0.0)
            Log.d("bbbb", "latitude: $latitude, longitude: $longitude, radius: $radius")
        }
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