package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities

@Entity(tableName = "saved_rooms")
data class SavedRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val widthCm: Int = 400,
    val lengthCm: Int = 400,
    val itemsJson: String = "[]", // Placed Items formatted as local JSON string
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val productName: String,
    val price: Double,
    val category: String,
    val quantity: Int = 1,
    val roomSetupGroup: String = "General Shopping", // Bedroom Set, Living Room Set, etc.
    val assemblyServiceAdded: Boolean = false // Assembly service add-on
)

// Placed item helper class for JSON representation on Room Planner
data class PlacedFurniture(
    val productId: String,
    val x: Int, // layout grid pixel or cm coordinate
    val y: Int, // layout grid pixel or cm coordinate
    val rotationDeg: Int = 0, // 0, 90, 180, 270
    val instanceId: String = System.currentTimeMillis().toString() + "_" + (100..999).random()
)

// 2. DAO (Data Access Object)

@Dao
interface HemmaDao {
    // Room queries
    @Query("SELECT * FROM saved_rooms ORDER BY timestamp DESC")
    fun getAllSavedRooms(): Flow<List<SavedRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: SavedRoom)

    @Delete
    suspend fun deleteRoom(room: SavedRoom)

    // Cart queries
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Update
    suspend fun updateCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

// 3. Database Abstract Class

@Database(entities = [SavedRoom::class, CartItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hemmaDao(): HemmaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hemma_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// 4. Repository

class HemmaRepository(private val dao: HemmaDao) {
    // Rooms
    val allSavedRooms: Flow<List<SavedRoom>> = dao.getAllSavedRooms()

    suspend fun saveRoom(room: SavedRoom) {
        dao.insertRoom(room)
    }

    suspend fun deleteRoom(room: SavedRoom) {
        dao.deleteRoom(room)
    }

    // Cart
    val cartItems: Flow<List<CartItem>> = dao.getCartItems()

    suspend fun addCartItem(product: Product, quantity: Int = 1, group: String = "General Shopping") {
        // Query to check if same product already exists in same group
        // Real-world simple check or insert:
        val item = CartItem(
            productId = product.id,
            productName = product.name,
            price = product.price,
            category = product.category,
            quantity = quantity,
            roomSetupGroup = group
        )
        dao.insertCartItem(item)
    }

    suspend fun updateCartItem(item: CartItem) {
        dao.updateCartItem(item)
    }

    suspend fun deleteCartItem(item: CartItem) {
        dao.deleteCartItem(item)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }
}
