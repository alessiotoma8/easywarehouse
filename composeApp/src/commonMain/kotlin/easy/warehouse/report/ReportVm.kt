package easy.warehouse.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import easy.warehouse.product.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReportVm : ViewModel() {
    private val productRepo = getRoomDatabase().getProductDao()
    private val employeeRepo = getRoomDatabase().getEmployeeDao()
    private val vehicleRepo = getRoomDatabase().getDestinationDao()
    private val reportRepo = getRoomDatabase().getReportDao()

    val reports = reportRepo.getAllReports()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createReport(productId: Long, employeeId: Long, vehiclePlate: String?, deltaProduct: Int) =
        viewModelScope.launch {
            val product = productRepo.getById(productId)
            val employee = employeeRepo.getById(employeeId)
            val vehicle = vehiclePlate?.let { vehicleRepo.getById(it) }

            val report = ReportEntity(
                date = ",",
                time = ",",
                employeeName = employee?.name.orEmpty(),
                employeeSurname = employee?.surname.orEmpty(),
                productTitle = product?.title.orEmpty(),
                productDesc = product?.content.orEmpty(),
                productUtility = product?.utility ?: Utility.ALTRI,
                productCountChange = deltaProduct,
                vehiclePlate = vehicle?.vehiclePlate,
                vehicleName = vehicle?.vehicleName
            )

            reportRepo.insertReport(report)
        }
}