package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // "Living Room", "Bedroom", "Kitchen", "Storage", "Lighting", "Outdoor"
    val price: Double,
    val widthCm: Int,
    val lengthCm: Int,
    val material: String,
    val color: String,
    val isFlatPack: Boolean = true,
    val assemblyDifficulty: Int, // 1 to 5
    val rating: Float, // e.g. 4.7f
    val reviewsCount: Int,
    val sustainabilityInfo: String,
    val matchingSetIds: List<String> = emptyList()
) {
    // Helper to represent visually
    val dimensionsDisplay: String
        get() = "${widthCm}W × ${lengthCm}D cm"
}

object ProductCatalog {
    val items = listOf(
        Product(
            id = "malm_bed",
            name = "MALM Bed Frame",
            description = "A clean design that's just as beautiful from all sides – place the bed freestanding or with the headboard against a wall. Includes integrated generous under-bed drawer storage options.",
            category = "Bedroom",
            price = 249.00,
            widthCm = 160,
            lengthCm = 200,
            material = "Fibreboard, Oak veneer",
            color = "White",
            isFlatPack = true,
            assemblyDifficulty = 4,
            rating = 4.6f,
            reviewsCount = 184,
            sustainabilityInfo = "All oak in our products comes from FSC®-certified forests, ensuring sustainable timber harvesting.",
            matchingSetIds = listOf("malm_chest", "nyma_lamp")
        ),
        Product(
            id = "malm_chest",
            name = "MALM Drawer Chest",
            description = "A clean look that fits right in, in the bedroom or wherever you place it. Smooth-running drawers with pull-out stop.",
            category = "Bedroom",
            price = 119.00,
            widthCm = 80,
            lengthCm = 48,
            material = "Particleboard, Ash veneer",
            color = "White",
            isFlatPack = true,
            assemblyDifficulty = 3,
            rating = 4.5f,
            reviewsCount = 312,
            sustainabilityInfo = "Constructed with at least 50% renewable wood materials.",
            matchingSetIds = listOf("malm_bed")
        ),
        Product(
            id = "billy_bookcase",
            name = "BILLY Bookcase",
            description = "The world's most versatile bookcase. It is estimated that one BILLY bookcase is sold somewhere in the world every five seconds. Excellent option for living room, kids room, or storage study.",
            category = "Storage",
            price = 59.00,
            widthCm = 80,
            lengthCm = 28,
            material = "Particleboard, Paper foil, Plastic edging",
            color = "Birch Plywood",
            isFlatPack = true,
            assemblyDifficulty = 2,
            rating = 4.8f,
            reviewsCount = 824,
            sustainabilityInfo = "Paper used is sourced from responsible forest management.",
            matchingSetIds = listOf("kallax_shelving", "lack_table")
        ),
        Product(
            id = "kallax_shelving",
            name = "KALLAX Shelving Unit",
            description = "Standing or lying – the KALLAX series adapts to taste, space, budget and needs. Smooth surfaces and rounded corners give a feel of quality, customizable with baskets and drawers.",
            category = "Storage",
            price = 89.00,
            widthCm = 77,
            lengthCm = 147,
            material = "Particleboard, Wood fibreboard",
            color = "Natural Oak",
            isFlatPack = true,
            assemblyDifficulty = 3,
            rating = 4.7f,
            reviewsCount = 592,
            sustainabilityInfo = "100% recycled paperboard filling is used as core structures.",
            matchingSetIds = listOf("billy_bookcase", "lack_table")
        ),
        Product(
            id = "poang_chair",
            name = "POÄNG Armchair",
            description = "Layer-glued bent oak frame gives comfortable resilience. Elegant curves, high backrest, and classic beige fabric cushion that has supported tired backs for over forty years.",
            category = "Living Room",
            price = 129.00,
            widthCm = 68,
            lengthCm = 82,
            material = "Bentwood, 100% Cotton cushion",
            color = "Beige",
            isFlatPack = true,
            assemblyDifficulty = 2,
            rating = 4.9f,
            reviewsCount = 418,
            sustainabilityInfo = "Cotton cushion lining made from 100% cotton from sustainable sources.",
            matchingSetIds = listOf("stockholm_rug", "lack_table")
        ),
        Product(
            id = "lack_table",
            name = "LACK Coffee Table",
            description = "Easy to assemble, lift and move around. A practical shelf under the tabletop helps to keep newspapers and coasters sorted and the top clean and clear.",
            category = "Living Room",
            price = 29.00,
            widthCm = 90,
            lengthCm = 55,
            material = "Particleboard, Honeycomb paper corefill",
            color = "Beige Wood",
            isFlatPack = true,
            assemblyDifficulty = 1,
            rating = 4.3f,
            reviewsCount = 673,
            sustainabilityInfo = "Honeycomb paper core helps reduce timber weight by 60%.",
            matchingSetIds = listOf("poang_chair", "billy_bookcase")
        ),
        Product(
            id = "stockholm_rug",
            name = "STOCKHOLM Flatwoven Rug",
            description = "Hand-woven by skilled craftspeople, making each rug unique. Durable, soil-resistant wool surface makes this classic striped rug perfect in your dining or living room.",
            category = "Living Room",
            price = 229.00,
            widthCm = 170,
            lengthCm = 240,
            material = "100% Pure New Wool",
            color = "Classic Stripe Black/White",
            isFlatPack = false,
            assemblyDifficulty = 1,
            rating = 4.8f,
            reviewsCount = 115,
            sustainabilityInfo = "Handwoven in India supporting local weaving cooperatives with fair wages.",
            matchingSetIds = listOf("poang_chair")
        ),
        Product(
            id = "nyma_lamp",
            name = "NYMÖ Pendant Lamp Shade",
            description = "Add a touch of Scandinavian modernism. When lit, light spreads through the perforated shade, creating beautiful decorative reflections on surrounding walls.",
            category = "Lighting",
            price = 35.00,
            widthCm = 44,
            lengthCm = 44,
            material = "Steel, Epoxy powder coating",
            color = "Black with Brass Inner",
            isFlatPack = true,
            assemblyDifficulty = 2,
            rating = 4.6f,
            reviewsCount = 98,
            sustainabilityInfo = "Can be taken apart for recovery or recycling.",
            matchingSetIds = listOf("malm_bed")
        ),
        Product(
            id = "ranarp_work_lamp",
            name = "RANARP Desk Work Lamp",
            description = "With look of vintage metal, heavy base, and adjustable mechanical joints, this steel lamp provides perfectly focused reading light for creative home offices.",
            category = "Lighting",
            price = 39.99,
            widthCm = 28,
            lengthCm = 28,
            material = "Steel, Powder coat",
            color = "Off-White",
            isFlatPack = true,
            assemblyDifficulty = 1,
            rating = 4.7f,
            reviewsCount = 142,
            sustainabilityInfo = "Optimized for high-efficiency low-wattage LED retrofitting.",
            matchingSetIds = listOf("alex_desk")
        ),
        Product(
            id = "anvandar_bench",
            name = "ÄNVÄNDAR Outdoor Bench",
            description = "Solid light acacia wood bench perfect for a sunny outdoor patio, balcony, or garden conservatory. Naturally weather-resistant and matures into a silver gray patina.",
            category = "Outdoor",
            price = 149.00,
            widthCm = 120,
            lengthCm = 50,
            material = "Solid Acacia Wood",
            color = "Light Brown",
            isFlatPack = true,
            assemblyDifficulty = 3,
            rating = 4.4f,
            reviewsCount = 67,
            sustainabilityInfo = "100% sourced from verified sustained tropical wood farms.",
            matchingSetIds = listOf("nyma_lamp")
        ),
        Product(
            id = "alex_desk",
            name = "ALEX Writing Desk",
            description = "A clean-looking study desk that fits anywhere. Built-in cable management compartment at the back hides untidy wire cords while keeping them within easy reach.",
            category = "Storage",
            price = 169.00,
            widthCm = 131,
            lengthCm = 60,
            material = "Wood fibreboard, Acrylic paint",
            color = "White",
            isFlatPack = true,
            assemblyDifficulty = 4,
            rating = 4.7f,
            reviewsCount = 280,
            sustainabilityInfo = "Sourced FSC wood board.",
            matchingSetIds = listOf("ranarp_work_lamp")
        )
    )

    fun getProductById(id: String): Product? = items.find { it.id == id }

    fun filterProducts(
        category: String?,
        maxPrice: Double?,
        difficulty: Int?,
        styleFilter: String?
    ): List<Product> {
        return items.filter { product ->
            if (category != null && product.category != category) return@filter false
            if (maxPrice != null && product.price > maxPrice) return@filter false
            if (difficulty != null && product.assemblyDifficulty > difficulty) return@filter false
            // Style filter simulation
            if (styleFilter != null) {
                val matchesStyle = when (styleFilter) {
                    "Scandi" -> product.material.contains("Oak", true) || product.color == "White" || product.color == "Birch Plywood"
                    "Minimalist" -> product.color == "White" || product.id.contains("lack") || product.id.contains("alex")
                    "Industrial" -> product.material.contains("Steel", true) || product.color.contains("Black")
                    else -> true
                }
                if (!matchesStyle) return@filter false
            }
            true
        }
    }
}
