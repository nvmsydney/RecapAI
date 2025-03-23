package com.recapai.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient {
    companion object {
        private const val DEEPGRAM_BASE_URL = "https://api.deepgram.com/"
        const val DEEPGRAM_API_KEY = ""
        private const val OPENAI_BASE_URL = "https://api.openai.com/"
        const val OPENAI_API_KEY = ""

        // Create Deepgram service instance
        fun create(): DeepgramService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(DEEPGRAM_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DeepgramService::class.java)
        }

        // Create request body for Deepgram API
        fun createRequestBody(url: String): Map<String, String> {
            return mapOf("url" to url)
        }

        // Create OpenAI service instance
        fun createOpenAIClient(): OpenAIService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(OPENAI_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService::class.java)
        }
    }
}
