package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GeminiPart(val text: String? = null)

@JsonClass(generateAdapter = true)
data class GeminiContent(val parts: List<GeminiPart>)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(val content: GeminiContent? = null)

@JsonClass(generateAdapter = true)
data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

class GeminiDesignerService {
    private val systemInstruction = """
        You are HEMMA, a brilliant, friendly Scandinavian Interior Design Assistant inspired by IKEA. 
        Your goal is to guide and advise users on room design, furnishing items, spatial layout hacks, 
        and styling (e.g., Scandi, Minimalist, Industrial, Rustic).
        
        Available HEMMA furniture products in our catalog:
        1. "malm_bed" (MALM Bed Frame, price: $249) - Dimensions: 160x200cm, Bedroom
        2. "malm_chest" (MALM Drawer Chest, price: $119) - Dimensions: 80x48cm, Bedroom
        3. "billy_bookcase" (BILLY Bookcase, price: $59) - Dimensions: 80x28cm, Storage / Office
        4. "kallax_shelving" (KALLAX Shelving Unit, price: $89) - Dimensions: 77x147cm, Storage / Room Divider
        5. "poang_chair" (POÄNG Armchair, price: $129) - Dimensions: 68x82cm, Living Room
        6. "lack_table" (LACK Coffee Table, price: $29) - Dimensions: 90x55cm, Living Room
        7. "stockholm_rug" (STOCKHOLM Flatwoven Rug, price: $229) - Dimensions: 170x240cm, Living Room / Rug
        8. "nyma_lamp" (NYMÖ Pendant Lamp Shade, price: $35) - Dimensions: 44x44cm, Lighting
        9. "ranarp_work_lamp" (RANARP Desk Work Lamp, price: $39.99) - Dimensions: 28x28cm, Lighting / Office
        10. "anvandar_bench" (ÄNVÄNDAR Outdoor Bench, price: $149) - Dimensions: 120x50cm, Outdoor
        11. "alex_desk" (ALEX Writing Desk, price: $169) - Dimensions: 131x60cm, Office / Storage
        
        When suggesting layouts or items, recommend these specific products by writing their ID in square brackets, like [malm_bed] or [billy_bookcase], so the app UI can dynamically link and render interactive "Shop item" shortcuts on screen!
        Be concise, helpful, Swedish-cool, design-focused, and practical. Focus on maximizing natural light, wood textures, and space saving hacks.
    """.trimIndent()

    suspend fun consultDesigner(userPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Emulate local offline IKEA designer fallback with smart responses to ensure full offline usefulness!
            return@withContext offlineAIResponse(userPrompt)
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstruction)))
        )

        try {
            val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I apologize, I lost my pencil for a second. Could you repeat that? (Alternatively, check if your Gemini API key is active in the Secrets configurations.)"
        } catch (e: Exception) {
            e.printStackTrace()
            "Hej! Connecting to our Stockholm design servers had a quick bump: ${e.message ?: "network issue"}. Here is an offline design suggestion:\n\n" + offlineAIResponse(userPrompt)
        }
    }

    private fun offlineAIResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("bedroom") || lower.contains("sleep") || lower.contains("cosy") -> {
                "Hej! To create a cosy Scandinavian Sanctuary, I highly recommend placing a [malm_bed] centered in your bedroom. This gives symmetry. Next to it, anchor with a [malm_chest] for crisp storage. Drizzle the scene in warm glow using the [nyma_lamp]. Focus on neutral whites, light woods, and simple bed linen."
            }
            lower.contains("office") || lower.contains("work") || lower.contains("desk") || lower.contains("study") -> {
                "Hej! For an energized, flat-pack productive home office, let's pair our sturdy [alex_desk] with a focused [ranarp_work_lamp] to direct reading rays exactly where your notes are. If you have extra wall space, align a [billy_bookcase] to neatly showcase your design folders."
            }
            lower.contains("living") || lower.contains("sofa") || lower.contains("chair") || lower.contains("warm") || lower.contains("bigger") -> {
                "Hej! To make your living room feel immensely brighter and larger, layer a [stockholm_rug] in monochrome black-and-white stripes over light wooden floors. Place a comfortable [poang_chair] in the corner near natural window light, and place a [lack_table] alongside to hold tea cups. Keep walls off-white to bounce natural sunshine!"
            }
            lower.contains("outside") || lower.contains("outdoor") || lower.contains("balcony") || lower.contains("garden") -> {
                "Hej! Let's build a Scandinavian patio. Use our rustic acacia [anvandar_bench] decorated with some beige weatherproof outdoor cushions. Hang a [nyma_lamp] nearby for comfortable twilight gatherings."
            }
            else -> {
                "Hej! I am HEMMA, your interior planning assistant. I can suggest stylish setups for your bedroom (using [malm_bed], [malm_chest]), living room (featuring [poang_chair], [stockholm_rug], [lack_table]), office space ([alex_desk], [ranarp_work_lamp]), or storage designs using [billy_bookcase] or [kallax_shelving].\n\nWhat space are you looking to optimise today?"
            }
        }
    }
}
