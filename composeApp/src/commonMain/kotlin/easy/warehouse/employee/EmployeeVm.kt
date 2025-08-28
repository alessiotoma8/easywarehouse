package easy.warehouse.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeVm : ViewModel() {

    private val dbRepo = getRoomDatabase().getEmployeeDao()
    private val _employees = MutableStateFlow<List<EmployeeEntity>>(emptyList())
    val employees: StateFlow<List<EmployeeEntity>> = _employees.asStateFlow()

    private val reportDbRepo = getRoomDatabase().getReportDao()

    init {
        viewModelScope.launch {
            _employees.value = dbRepo.selectAll()
        }
    }

    fun addEmployee(name: String, surname: String) = viewModelScope.launch {
        val newEmployee = EmployeeEntity(name = name, surname = surname)
        dbRepo.insert(newEmployee)
        _employees.value = dbRepo.selectAll()
    }

    fun removeEmployee(id: Long) = viewModelScope.launch {
        dbRepo.removeById(id)
        _employees.value = dbRepo.selectAll()
    }

    fun updateEmployee(name: String, surname: String, id:Long) = viewModelScope.launch {
        val newEmployee = EmployeeEntity(name = name, surname = surname, id= id)
        dbRepo.insert(newEmployee)
        _employees.value = dbRepo.selectAll()

        val reports = reportDbRepo.getAllReportsList()
        reports.filter { it.employeeId == newEmployee.id }.forEach { report ->
            val newReport = report.copy(
                employeeName = name, employeeSurname = surname
            )
            newReport.let { reportDbRepo.insertReport(it) }
        }
    }
}