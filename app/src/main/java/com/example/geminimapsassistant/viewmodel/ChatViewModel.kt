package com.example.geminimapsassistant.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminimapsassistant.data.ChatMessage
import com.example.geminimapsassistant.data.GeminiRepository
import com.example.geminimapsassistant.data.RouteInfo
import com.google.android.gms.location.LocationServices
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Demo amaçlı örnek konum koordinatları (gerçek üründe Directions API'den gelir)
private val DEMO_DESTINATIONS = mapOf(
    "Taksim Meydanı" to GeoPoint(41.0370, 28.9850),
    "Kadıköy" to GeoPoint(40.9906, 29.0274),
    "Beşiktaş" to GeoPoint(41.0422, 29.0061)
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GeminiRepository()
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf() // Karşılama ekranı boş sohbetle başlar
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentRoute = MutableStateFlow<RouteInfo?>(null)
    val currentRoute: StateFlow<RouteInfo?> = _currentRoute.asStateFlow()

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    private fun timestamp(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    /**
     * MainActivity, konum izni verildikten sonra bu fonksiyonu çağırır.
     */
    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _userLocation.value = GeoPoint(location.latitude, location.longitude)
            } else {
                // Konum alınamazsa İstanbul merkezine yakın bir varsayılan nokta kullan
                _userLocation.value = GeoPoint(41.0082, 28.9784)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.value = _messages.value + ChatMessage(text, isFromUser = true, timestamp = timestamp())
        _isLoading.value = true

        viewModelScope.launch {
            val reply = repository.sendMessage(text)
            _messages.value = _messages.value + ChatMessage(reply, isFromUser = false, timestamp = timestamp())
            _isLoading.value = false

            // Kullanıcı bir yer adı belirttiyse demo rota oluştur
            val destinationName = repository.extractDestinationKeyword(text)
            if (destinationName != null) {
                val destinationLatLng = DEMO_DESTINATIONS[destinationName]
                val origin = _userLocation.value
                if (destinationLatLng != null && origin != null) {
                    _currentRoute.value = RouteInfo(
                        origin = origin,
                        destination = destinationLatLng,
                        destinationLabel = destinationName,
                        durationText = "18 dk",
                        distanceText = "6,2 km",
                        trafficStatus = "Trafik normal"
                    )
                }
            }
        }
    }

    fun clearRoute() {
        _currentRoute.value = null
    }
}
