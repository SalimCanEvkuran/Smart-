package com.example.geminimapsassistant.data

import org.osmdroid.util.GeoPoint

/**
 * Tek bir sohbet mesajını temsil eder.
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

/**
 * Gemini'nin oluşturduğu veya kullanıcının istediği rota bilgisi.
 * Gerçek yol tarifi verisi için bir yol tarifi servisi (örn. OSRM) kullanılabilir;
 * burada basit bir gösterim modeli tanımlıyoruz.
 */
data class RouteInfo(
    val origin: GeoPoint,
    val destination: GeoPoint,
    val destinationLabel: String,
    val durationText: String,   // örn. "18 dk"
    val distanceText: String,   // örn. "6,2 km"
    val trafficStatus: String   // örn. "Trafik normal"
)
