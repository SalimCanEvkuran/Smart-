package com.example.geminimapsassistant.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geminimapsassistant.ui.theme.BackgroundDark
import com.example.geminimapsassistant.ui.theme.SurfaceDark
import com.example.geminimapsassistant.ui.theme.TextPrimary
import com.example.geminimapsassistant.viewmodel.ChatViewModel

/**
 * Taslaktaki 3 senaryoyu tek akışta birleştirir:
 * - Dikey + rota yok  -> tam ekran sohbet (Senaryo 1)
 * - Dikey + rota var  -> üstte harita kartı, altta sohbet (Senaryo 2)
 * - Yatay mod         -> sol sohbet, sağ harita her zaman görünür (Senaryo 3)
 */
@Composable
fun AssistantApp(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val route by viewModel.currentRoute.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        TopBar(showBack = route != null || messages.isNotEmpty())

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                ChatScreen(
                    messages = messages,
                    isLoading = isLoading,
                    onSendMessage = viewModel::sendMessage,
                    modifier = Modifier.weight(1f)
                )
                MapScreen(
                    userLocation = userLocation,
                    route = route,
                    onStartNavigation = { /* Google Haritalar navigasyonunu tetikle */ },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                if (route != null) {
                    MapScreen(
                        userLocation = userLocation,
                        route = route,
                        onStartNavigation = { /* Google Haritalar navigasyonunu tetikle */ },
                        modifier = Modifier.weight(0.55f)
                    )
                }
                ChatScreen(
                    messages = messages,
                    isLoading = isLoading,
                    onSendMessage = viewModel::sendMessage,
                    modifier = Modifier.weight(if (route != null) 0.45f else 1f)
                )
            }
        }
    }
}

@Composable
private fun TopBar(showBack: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = { /* Geri navigasyonu */ }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Geri", tint = TextPrimary)
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }
        Text("Sohbet", color = TextPrimary, modifier = Modifier.weight(1f))
    }
}
