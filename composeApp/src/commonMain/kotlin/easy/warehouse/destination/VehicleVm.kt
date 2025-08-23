package easy.warehouse.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import easy.warehouse.employee.EmployeeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleVm: ViewModel()  {

    private val dbRepo = getRoomDatabase().getDestinationDao()
    private val _vehicles = MutableStateFlow<List<VehicleDestinationEntity>>(emptyList())
    val vehicles: StateFlow<List<VehicleDestinationEntity>> = _vehicles.asStateFlow()

    init {
        viewModelScope.launch {
            _vehicles.value = dbRepo.selectAll()
        }
    }

    fun addVehicle(name: String, plate: String) = viewModelScope.launch {
        val newVehicle = VehicleDestinationEntity(vehicleName = name, vehiclePlate = plate)
        dbRepo.insert(newVehicle)
        _vehicles.value = dbRepo.selectAll()
    }
}