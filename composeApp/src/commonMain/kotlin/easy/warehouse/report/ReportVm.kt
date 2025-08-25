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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.filter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

data class InventoryItem(
    val employeeName: String,
    val employeeSurname: String,
    val productTitle: String,
    val totalCount: Int
)

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
            val tz = TimeZone.currentSystemDefault()
            val now = Clock.System.now()

            val todayStart = now.toLocalDateTime(tz).date.atStartOfDayIn(tz)

            val startInstant = todayStart.minus(it, tz)

            val endInstant = todayStart
                .plus(1, DateTimeUnit.DAY, tz)
                .minus(1.seconds)

            reports.filter { report ->
                val instant = report.getInstant()
                instant >= startInstant && instant <= endInstant
            }
        }?:reports
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())



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

    fun generateInventory(reports: List<ReportEntity>): List<InventoryItem> {
        return reports
            .groupBy { it.employeeName to it.employeeSurname } // raggruppa per utente
            .flatMap { (_, userReports) ->
                userReports
                    .groupBy { it.productTitle } // raggruppa per prodotto
                    .map { (product, productReports) ->
                        InventoryItem(
                            employeeName = userReports.first().employeeName,
                            employeeSurname = userReports.first().employeeSurname,
                            productTitle = product,
                            totalCount = productReports.sumOf { it.productCountChange } // somma dei delta
                        )
                    }.filter{it.totalCount > 0}
            }
    }

    fun filterByDatePeriod(period: DateTimePeriod?) = viewModelScope.launch {
        _selectedDatePeriod.emit(period)
    }
}