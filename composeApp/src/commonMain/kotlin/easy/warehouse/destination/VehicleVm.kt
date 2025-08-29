package easy.warehouse.destination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehicleVm : ViewModel() {

    private val dbRepo = getRoomDatabase().getDestinationDao()
    private val _vehicles = MutableStateFlow<List<VehicleDestinationEntity>>(emptyList())
    val vehicles: StateFlow<List<VehicleDestinationEntity>> = _vehicles.asStateFlow()

    val reportDbRepo = getRoomDatabase().getReportDao()

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

    fun removeVehicle(id: Long) = viewModelScope.launch {
        dbRepo.removeById(id)
        _vehicles.value = dbRepo.selectAll()
    }

    fun updateVehicle(name: String, plate: String, id:Long) = viewModelScope.launch {
        val newVehicle = VehicleDestinationEntity(vehicleName = name, vehiclePlate = plate, id = id)
        dbRepo.insert(newVehicle)
        _vehicles.value = dbRepo.selectAll()

        val reports = reportDbRepo.getAllReportsList()
        reports.filter { it.vehiclePlate == plate }.forEach { report ->
            val newReport = report.copy(
                vehiclePlate = plate, vehicleName = name
            )
            newReport.let { reportDbRepo.insertReport(it) }
        }

    }
}