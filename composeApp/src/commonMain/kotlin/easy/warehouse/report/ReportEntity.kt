package easy.warehouse.report

import androidx.room.Entity
import androidx.room.PrimaryKey
import easy.warehouse.product.Utility

@Entity
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val time: String,
    val employeeName: String,
    val employeeSurname: String,
    val productTitle: String,
    val productDesc: String,
    val productUtility: Utility,
    val productCountChange: Int, // può essere positivo (aggiunta) o negativo (rimozione)
    val vehiclePlate: String? = null,
    val vehicleName: String? = null,
)