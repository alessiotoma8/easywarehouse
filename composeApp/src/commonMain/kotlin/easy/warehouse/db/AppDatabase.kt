package easy.warehouse.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import easy.warehouse.destination.VehicleDestDao
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.employee.EmployeeDao
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.product.Converters
import easy.warehouse.product.ProductDao
import easy.warehouse.product.ProductEntity

@Database(entities = [ProductEntity::class, EmployeeEntity::class, VehicleDestinationEntity::class], version = 1)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getProductDao(): ProductDao
    abstract fun getEmployeeDao(): EmployeeDao
    abstract fun getDestinationDao(): VehicleDestDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}