package easy.warehouse.destination

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import easy.warehouse.employee.EmployeeEntity
@Dao
interface VehicleDestDao {
    @Insert
    suspend fun insert(item: VehicleDestinationEntity)
    @Query("SELECT * FROM VehicleDestinationEntity ORDER BY vehicleName DESC")
    suspend fun selectAll(): List<VehicleDestinationEntity>

    @Query("DELETE FROM VehicleDestinationEntity WHERE vehiclePlate = :vehiclePlate")
    suspend fun removeById(vehiclePlate: String)

    @Query("SELECT * FROM VehicleDestinationEntity WHERE vehiclePlate = :vehiclePlate")
    suspend fun getById(vehiclePlate: String): VehicleDestinationEntity?
}