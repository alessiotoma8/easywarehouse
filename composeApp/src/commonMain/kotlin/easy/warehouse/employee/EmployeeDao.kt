package easy.warehouse.employee

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(item: EmployeeEntity)
    @Query("SELECT * FROM EmployeeEntity ORDER BY name DESC")
    suspend fun selectAll(): List<EmployeeEntity>

    @Query("DELETE FROM EmployeeEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM EmployeeEntity WHERE id = :id")
    suspend fun getById(id: Long): EmployeeEntity?
}