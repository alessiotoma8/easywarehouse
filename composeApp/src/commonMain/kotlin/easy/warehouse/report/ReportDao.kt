package easy.warehouse.report

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM ReportEntity ORDER BY id DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM ReportEntity ORDER BY id DESC")
    suspend fun getAllReportsList(): List<ReportEntity>

    @Query("DELETE FROM ReportEntity")
    suspend fun deleteAllReports()
}