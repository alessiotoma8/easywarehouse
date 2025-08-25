package easy.warehouse.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import easy.warehouse.product.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.filter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ReportVm : ViewModel() {
    private val productRepo = getRoomDatabase().getProductDao()
    private val employeeRepo = getRoomDatabase().getEmployeeDao()
    private val vehicleRepo = getRoomDatabase().getDestinationDao()
    private val reportRepo = getRoomDatabase().getReportDao()

    private val _selectedDatePeriod = MutableStateFlow<DateTimePeriod?>(null)
    val selectedDatePeriod = _selectedDatePeriod.asStateFlow()

    val reports = reportRepo.getAllReports().combine(selectedDatePeriod){ reports, period->
        period?.let {
            val now = Clock.System.now()
            val startInstant = now.minus(period, TimeZone.currentSystemDefault())
            reports.filter { report ->
                val reportInstant = report.getInstant()
                reportInstant >= startInstant && reportInstant <= now
            }
        }?:reports
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    suspend fun getUserInventory(employeeId: Long): Map<String, Int> {
        val reports = reportRepo.getAllReportsByEmployee(employeeId)

        return reports
            .groupBy { it.productTitle }
            .mapValues { (_, productReports) ->
                productReports.sumOf { it.productCountChange }
            }
            .filterValues { it != 0 } // opzionale, mostra solo i prodotti che ha ancora
    }


    @OptIn(ExperimentalTime::class)
    fun createReport(productId: Long, employeeId: Long, vehiclePlate: String?, deltaProduct: Int) =
        viewModelScope.launch {
            val product = productRepo.getById(productId)
            val employee = employeeRepo.getById(employeeId)
            val vehicle = vehiclePlate?.let { vehicleRepo.getById(it) }

            val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val report = ReportEntity(
                date = date.date.toString(),
                time = date.time.toString(),
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

    fun filterByDatePeriod(period: DateTimePeriod?) = viewModelScope.launch {
        _selectedDatePeriod.emit(period)
    }
}