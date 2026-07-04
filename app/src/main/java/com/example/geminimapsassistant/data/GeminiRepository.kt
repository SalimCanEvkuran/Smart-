package com.example.geminimapsassistant.data

import com.example.geminimapsassistant.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

/**
 * Gemini modeliyle sohbet mantığını yöneten sınıf.
 * API anahtarı local.properties -> BuildConfig üzerinden okunur, kod içine yazılmaz.
 */
class GeminiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
        },
        systemInstruction = com.google.ai.client.generativeai.type.content {
            text(
                "Sen bir Android haritalar asistanısın. Kullanıcıya kısa, net ve " +
                "Türkçe yanıtlar ver. Kullanıcı bir yere gitmek istediğini belirtirse " +
                "rota oluşturduğunu ve tahmini süre/mesafe bilgisi vereceğini söyle."
            )
        }
    )

    /**
     * Kullanıcı mesajını Gemini'ye gönderir ve metin cevabını döner.
     */
    suspend fun sendMessage(userMessage: String): String {
        return try {
            val response = generativeModel.generateContent(userMessage)
            response.text ?: "Üzgünüm, bir cevap oluşturamadım."
        } catch (e: Exception) {
            "Bağlantı hatası: ${e.localizedMessage ?: "bilinmeyen hata"}"
        }
    }

    /**
     * Basit bir anahtar kelime kontrolü ile kullanıcının rota istediğini tespit eder.
     * Gerçek bir üründe bu kısım Gemini'nin function calling özelliğiyle ve
     * Google Directions API ile değiştirilmelidir (gerçek süre/mesafe için).
     */
    fun extractDestinationKeyword(userMessage: String): String? {
        val lower = userMessage.lowercase()
        return when {
            "taksim" in lower -> "Taksim Meydanı"
            "kadıköy" in lower -> "Kadıköy"
            "beşiktaş" in lower -> "Beşiktaş"
            else -> null
        }
    }
}
