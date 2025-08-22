package easy.warehouse.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(item: ProductEntity)

    @Query("SELECT count(*) FROM ProductEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM ProductEntity")
    fun getAllAsFlow(): Flow<List<ProductEntity>>
}