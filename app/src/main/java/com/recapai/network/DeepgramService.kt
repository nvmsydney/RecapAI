package com.recapai.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepgramService {
    // API call to transcribe audio
    @POST("/v1/listen?language=en&model=nova-3")
    suspend fun transcribeAudio(
        @Header("Authorization") auth: String,
        @Body body: Map<String, String>
    ): Response<DeepgramResponse>
}

// Data classes to parse Deepgram API response
data class DeepgramResponse(
    val results: Results?
)

data class Results(
    val channels: List<Channel>
)

data class Channel(
    val alternatives: List<Alternative>
)

data class Alternative(
    val transcript: String
)
