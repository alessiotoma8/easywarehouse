package easy.warehouse.report

import androidx.room.Entity
import androidx.room.PrimaryKey
import easy.warehouse.product.Utility
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Entity
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val time: String,
    val employeeId:Long,
    val employeeName: String,
    val employeeSurname: String,
    val productId: Long,
    val productTitle: String,
    val productDesc: String?,
    val productUtility: Utility,
    val productCountChange: Int, // può essere positivo (aggiunta) o negativo (rimozione)
    val vehiclePlate: String? = null,
    val vehicleName: String? = null,
)

@OptIn(ExperimentalTime::class)
fun ReportEntity.getLocalDateTime() =
    Instant.parse(date + "T" + time + "Z").toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun ReportEntity.getInstant() =
    Instant.parse(date + "T" + time + "Z")