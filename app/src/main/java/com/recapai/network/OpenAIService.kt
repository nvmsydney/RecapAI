// app/src/main/java/com/recapai/network/OpenAIService.kt
package com.recapai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAIService {
    // API call to create summary
    @POST("/v1/chat/completions")
    suspend fun createSummary(
        @Header("Authorization") auth: String,
        @Body request: OpenAIRequest
    ): Response<OpenAIResponse>
}

// Data classes to create OpenAI API request and parse response
data class OpenAIRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String = "user",
    val content: String
)

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
