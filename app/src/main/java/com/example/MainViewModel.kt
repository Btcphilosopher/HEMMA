package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Catalogue : Screen()
    object Planner : Screen()
    object AIAssistant : Screen()
    object Cart : Screen()
    object Profile : Screen()
}

data class ChatMessage(
    val sender: String, // "User" or "HEMMA"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = HemmaRepository(db.hemmaDao())

    // Unified database feeds
    val savedRooms: StateFlow<List<SavedRoom>> = repository.allSavedRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Screen Routing State
    val currentScreen = MutableStateFlow<Screen>(Screen.Home)

    // Product Detail Sheet State
    val selectedProductDetail = MutableStateFlow<Product?>(null)

    // Catalogue Filters State
    val selectedCategory = MutableStateFlow<String?>(null)
    val maxPriceFilter = MutableStateFlow<Double?>(null)
    val maxDifficultyFilter = MutableStateFlow<Int?>(null)
    val selectedStyleFilter = MutableStateFlow<String?>(null)
    
    // Postcode Check State
    val postcodeQuery = MutableStateFlow("")
    val deliveryEligibility = MutableStateFlow<Boolean?>(null) // null = not checked

    // Active Room Planner State
    val activeRoom = MutableStateFlow<SavedRoom?>(null)
    val plcItemsState = MutableStateFlow<List<PlacedFurniture>>(emptyList())
    val selectedPlacementIndex = MutableStateFlow<Int?>(null)

    // Moshi adapter for Serializing placements
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, PlacedFurniture::class.java)
    private val placementAdapter = moshi.adapter<List<PlacedFurniture>>(listType)

    // AI Chat assist State
    val chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("HEMMA", "Hej! I am HEMMA, your Scandinavian interior designer. Tell me about your space or click any suggestion below! I can instantly recommend complete flat-pack friendly room setups.")
    ))
    val isAILoading = MutableStateFlow(false)
    private val geminiService = GeminiDesignerService()

    // Style quiz State
    val quizAnswers = MutableStateFlow<Map<Int, String>>(emptyMap()) // Q index -> Answer
    val quizCompleteResult = MutableStateFlow<String?>(null) // e.g. "Scandinavian Minimalist"

    init {
        // Automatically save initial Room Design to Room DB so first-time users have beautiful room ready to open!
        viewModelScope.launch {
            repository.allSavedRooms.collect { existing ->
                if (existing.isEmpty()) {
                    val initialPlacements = listOf(
                        PlacedFurniture("malm_bed", 120, 80, 0),
                        PlacedFurniture("malm_chest", 40, 200, 90),
                        PlacedFurniture("nyma_lamp", 180, 150, 0)
                    )
                    val json = try { placementAdapter.toJson(initialPlacements) } catch(e: Exception) { "[]" }
                    val demoRoom = SavedRoom(
                        name = "My Cozy Studio (Demo)",
                        widthCm = 360,
                        lengthCm = 400,
                        itemsJson = json
                    )
                    launch {
                        repository.saveRoom(demoRoom)
                    }
                }
            }
        }
    }

    // --- NAVIGATION HELPERS ---
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun showProductDetails(product: Product?) {
        selectedProductDetail.value = product
    }

    // --- DATABASE ACTIONS ---
    fun createAndOpenNewRoom(name: String, widthCm: Int, lengthCm: Int) {
        viewModelScope.launch {
            val emptyRoom = SavedRoom(name = name, widthCm = widthCm, lengthCm = lengthCm, itemsJson = "[]")
            repository.saveRoom(emptyRoom)
            
            // Auto open it in the Planner
            // Since we ordered rooms by timestamp desc, we can pick the newly created or load active room
            activeRoom.value = emptyRoom
            plcItemsState.value = emptyList()
            selectedPlacementIndex.value = null
            navigateTo(Screen.Planner)
        }
    }

    fun loadRoomToPlanner(room: SavedRoom) {
        activeRoom.value = room
        selectedPlacementIndex.value = null
        val items = try {
            placementAdapter.fromJson(room.itemsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        plcItemsState.value = items
        navigateTo(Screen.Planner)
    }

    fun deleteRoom(room: SavedRoom) {
        viewModelScope.launch {
            repository.deleteRoom(room)
            if (activeRoom.value?.id == room.id) {
                activeRoom.value = null
                plcItemsState.value = emptyList()
            }
        }
    }

    // --- PLANNER DRAG AND SCALE ACTIONS ---
    fun addFurnitureToRoom(productId: String) {
        val currentList = plcItemsState.value.toMutableList()
        // Center placement
        val roomW = activeRoom.value?.widthCm ?: 400
        val roomL = activeRoom.value?.lengthCm ?: 400
        
        val newItem = PlacedFurniture(
            productId = productId,
            x = (roomW / 2) - 30,
            y = (roomL / 2) - 30,
            rotationDeg = 0
        )
        currentList.add(newItem)
        plcItemsState.value = currentList
        selectedPlacementIndex.value = currentList.size - 1
        saveCurrentRoomState()
    }

    fun updateActivePlacementPosition(x: Int, y: Int) {
        val index = selectedPlacementIndex.value ?: return
        val currentList = plcItemsState.value.toMutableList()
        if (index in currentList.indices) {
            val original = currentList[index]
            currentList[index] = original.copy(x = x, y = y)
            plcItemsState.value = currentList
            saveCurrentRoomState()
        }
    }

    fun rotateActivePlacement() {
        val index = selectedPlacementIndex.value ?: return
        val currentList = plcItemsState.value.toMutableList()
        if (index in currentList.indices) {
            val original = currentList[index]
            val nextRotation = (original.rotationDeg + 90) % 360
            currentList[index] = original.copy(rotationDeg = nextRotation)
            plcItemsState.value = currentList
            saveCurrentRoomState()
        }
    }

    fun removeActivePlacement() {
        val index = selectedPlacementIndex.value ?: return
        val currentList = plcItemsState.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            plcItemsState.value = currentList
            selectedPlacementIndex.value = null
            saveCurrentRoomState()
        }
    }

    fun deselectPlacement() {
        selectedPlacementIndex.value = null
    }

    fun selectPlacement(index: Int) {
        selectedPlacementIndex.value = index
    }

    private fun saveCurrentRoomState() {
        val room = activeRoom.value ?: return
        val json = try { placementAdapter.toJson(plcItemsState.value) } catch (e: Exception) { "[]" }
        viewModelScope.launch {
            val updated = room.copy(itemsJson = json, timestamp = System.currentTimeMillis())
            repository.saveRoom(updated)
            activeRoom.value = updated
        }
    }

    // Add whole Room Set to Cart with a specific group!
    fun addEntireRoomToCart() {
        val room = activeRoom.value ?: return
        viewModelScope.launch {
            plcItemsState.value.forEach { placement ->
                val prod = ProductCatalog.getProductById(placement.productId)
                if (prod != null) {
                    repository.addCartItem(prod, quantity = 1, group = "${room.name} Set")
                }
            }
        }
    }

    // --- SHOPPING CART ACTIONS ---
    fun addSingleProductToCart(product: Product, quantity: Int = 1, group: String = "General Shopping") {
        viewModelScope.launch {
            repository.addCartItem(product, quantity, group)
        }
    }

    fun removeCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.deleteCartItem(item)
        }
    }

    fun toggleAssemblyService(item: CartItem) {
        viewModelScope.launch {
            repository.updateCartItem(item.copy(assemblyServiceAdded = !item.assemblyServiceAdded))
        }
    }

    fun changeCartQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                repository.deleteCartItem(item)
            } else {
                repository.updateCartItem(item.copy(quantity = newQty))
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun verifyPostcode(postcode: String) {
        viewModelScope.launch {
            postcodeQuery.value = postcode
            // Simplified Swedish postcode or UK postcode logic
            val clean = postcode.replace(" ", "").uppercase()
            // Assume any legitimate looking postcode (length 3..7) is deliverable!
            deliveryEligibility.value = clean.isNotEmpty() && clean.length in 3..8
        }
    }

    // --- AI CHAT ACTIONS ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val updatedMessages = chatMessages.value.toMutableList()
        updatedMessages.add(ChatMessage("User", text))
        chatMessages.value = updatedMessages
        
        isAILoading.value = true
        viewModelScope.launch {
            val reply = geminiService.consultDesigner(text)
            val finalMessages = chatMessages.value.toMutableList()
            finalMessages.add(ChatMessage("HEMMA", reply))
            chatMessages.value = finalMessages
            isAILoading.value = false
        }
    }

    // --- STYLE QUIZ ACTIONS ---
    fun selectQuizAnswer(questionIndex: Int, answer: String) {
        val current = quizAnswers.value.toMutableMap()
        current[questionIndex] = answer
        quizAnswers.value = current
        
        if (current.size >= 3) {
            // Determine result!
            val wood = current[0] ?: "" // e.g., "Natural Oak", "White Finish", "Rustic Pine"
            val vibe = current[1] ?: "" // e.g., "Minimalist - Less is more", "Cosy - Warm textiles", "Industrial - Raw bricks"
            val plant = current[2] ?: "" // e.g., "Lush Green Ficus", "Desert Cactus", "Simple Tulips"
            
            val result = when {
                wood == "White Finish" && vibe.startsWith("Minimalist") -> "Scandinavian Modern Minimalist"
                wood == "Natural Oak" || vibe.startsWith("Cosy") -> "Organic Stockholm Warmth"
                vibe.startsWith("Industrial") -> "Copenhagen Industrial Modern"
                else -> "Eclectic Nordic Blend"
            }
            quizCompleteResult.value = result
        }
    }

    fun resetQuiz() {
        quizAnswers.value = emptyMap()
        quizCompleteResult.value = null
    }
}
