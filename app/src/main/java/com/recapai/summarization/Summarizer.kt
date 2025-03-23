// app/src/main/java/com/recapai/summarization/Summarizer.kt
package com.recapai.summarization

import android.util.Log
import com.recapai.network.OpenAIService
import com.recapai.network.OpenAIRequest
import com.recapai.network.Message
import com.recapai.network.OpenAIResponse
import com.recapai.network.NetworkClient
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import retrofit2.Response

class Summarizer(private val openAIService: OpenAIService) {

    // Generate summary from transcript
    suspend fun generateSummary(transcript: String): String {
        return try {
            val prompt = createPrompt(transcript)
            Log.d("OpenAI", "Sending request with prompt: $prompt")

            val response = withTimeout(60_000) {
                openAIService.createSummary(
                    auth = "Bearer ${NetworkClient.OPENAI_API_KEY}",
                    request = OpenAIRequest(messages = listOf(Message(content = prompt)))
                )
            }

            processResponse(response).also {
                Log.d("OpenAI", "Received summary: $it")
            }
        } catch (e: Exception) {
            Log.e("OpenAI", "Error generating summary", e)
            "Summary generation failed: ${e.message}"
        }
    }

    // Create prompt for OpenAI API
    private fun createPrompt(transcript: String): String {
        return """
            You are an expert meeting summarization assistant. Given a transcript of a meeting, 
            produce a clear and professional summary organized by topics discussed. For each topic, 
            include key points, decisions made, and action items. At the end, include key takeaways
            and any follow-ups, as well as other relevant information. Be concise, clear, and
            organized using bullet points where appropriate.
            
            TRANSCRIPT:
            $transcript
        """.trimIndent()
    }

    // Process response from OpenAI API
    private fun processResponse(response: Response<OpenAIResponse>): String {
        return if (response.isSuccessful) {
            response.body()?.choices?.firstOrNull()?.message?.content
                ?: "No summary generated"
        } else {
            "Error: ${response.code()} - ${response.errorBody()?.string()}"
        }
    }
}
