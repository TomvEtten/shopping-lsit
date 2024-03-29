package hva.nl.shoppinglist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductRoomDatabase : RoomDatabase() {

    abstract fun productDao() : ProductDao

    companion object {
        private const val DATABASE_NAME = "PRODUCT_DATABASE"

        @Volatile
        private var productRoomDatabaseInstance: ProductRoomDatabase? = null

        fun getDatabase(context: Context): ProductRoomDatabase? {
            if (productRoomDatabaseInstance != null) {
                return productRoomDatabaseInstance
            }
            synchronized(ProductRoomDatabase::class.java) {
                productRoomDatabaseInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductRoomDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return productRoomDatabaseInstance;

        }
    }

}