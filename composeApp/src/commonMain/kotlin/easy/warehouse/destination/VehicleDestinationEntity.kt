package easy.warehouse.destination

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VehicleDestinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehiclePlate: String,
    val vehicleName: String,
)