package easy.warehouse.destination

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VehicleDestinationEntity(
    @PrimaryKey
    val vehiclePlate: String,
    val vehicleName: String,
)