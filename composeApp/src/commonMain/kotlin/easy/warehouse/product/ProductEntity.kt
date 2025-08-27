package easy.warehouse.product

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String?,
    val utility: Utility,
    val count: Int,
)

enum class Utility(val displayName:String){
    RICERCA_PERDITE("Ricerca perdite"),
    RILIEVO("Rilievo"),
    STRUMENTI("Strumenti"),
    DPI("Dpi"),
    ALTRI("Altro"),
}

class Converters {

    @TypeConverter
    fun fromVehicleType(value: Utility): String {
        return value.name // salva come String
    }

    @TypeConverter
    fun toVehicleType(value: String): Utility {
        return Utility.valueOf(value)
    }
}