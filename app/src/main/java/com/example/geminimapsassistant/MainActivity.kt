package com.example.geminimapsassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.geminimapsassistant.ui.AssistantApp
import com.example.geminimapsassistant.ui.theme.GeminiMapsAssistantTheme
import com.example.geminimapsassistant.viewmodel.ChatViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            chatViewModel.fetchUserLocation()
        }
    }

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // osmdroid, kendi tile cache'i için bir user-agent ve depolama yolu ister.
        // Bunu ayarlamazsan harita karoları bazı sunucularda yüklenmeyebilir.
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid_prefs", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        setContent {
            GeminiMapsAssistantTheme {
                AssistantApp(viewModel = chatViewModel)
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            chatViewModel.fetchUserLocation()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
