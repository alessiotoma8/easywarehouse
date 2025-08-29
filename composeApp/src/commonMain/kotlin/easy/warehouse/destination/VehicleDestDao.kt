package easy.warehouse.destination

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VehicleDestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VehicleDestinationEntity)
    @Query("SELECT * FROM VehicleDestinationEntity ORDER BY vehicleName DESC")
    suspend fun selectAll(): List<VehicleDestinationEntity>

    @Query("DELETE FROM VehicleDestinationEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM VehicleDestinationEntity WHERE id = :id")
    suspend fun getById(id: Long): VehicleDestinationEntity?
}