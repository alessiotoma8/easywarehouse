package easy.warehouse.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import easy.warehouse.product.Utility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

data class InventoryItem(
    val employeeName: String,
    val employeeSurname: String,
    val productTitle: String,
    val productDesc: String?,
    val productUtility: String,
    val vehicleName: String?,
    val vehiclePlate: String?,
    val totalCount: Int,
)

@OptIn(ExperimentalTime::class)
class ReportVm : ViewModel() {
    private val productRepo = getRoomDatabase().getProductDao()
    private val employeeRepo = getRoomDatabase().getEmployeeDao()
    private val vehicleRepo = getRoomDatabase().getDestinationDao()
    private val reportRepo = getRoomDatabase().getReportDao()

    private val _selectedDatePeriod = MutableStateFlow<DateTimePeriod?>(null)
    val selectedDatePeriod = _selectedDatePeriod.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery = _searchQuery.asStateFlow()

    private val dbReports = reportRepo.getAllReports()
    val reports =
        combine(dbReports, selectedDatePeriod, searchQuery) { reports, period, searchQuery ->
            val periodFIlteredReport = period?.let {
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
            } ?: reports

            searchQuery?.let { query ->
                if (query.isBlank()) {
                    periodFIlteredReport
                } else {
                    periodFIlteredReport.filter {
                        it.productDesc?.contains(query, ignoreCase = true) ?: false ||
                                it.productTitle.contains(query, ignoreCase = true) ||
                                it.productUtility.displayName.contains(query, ignoreCase = true) ||
                                it.employeeName.contains(query, ignoreCase = true) ||
                                it.vehicleName?.contains(query, ignoreCase = true) ?: false ||
                                it.vehiclePlate?.contains(query, ignoreCase = true) ?: false ||
                                it.employeeSurname.contains(query, ignoreCase = true)
                    }
                }
            } ?: periodFIlteredReport
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


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
                employeeId = employeeId,
                employeeName = employee?.name.orEmpty(),
                employeeSurname = employee?.surname.orEmpty(),
                productId = productId,
                productTitle = product?.title.orEmpty(),
                productDesc = product?.content,
                productUtility = product?.utility ?: Utility.ALTRI,
                productCountChange = deltaProduct,
                vehiclePlate = vehicle?.vehiclePlate,
                vehicleName = vehicle?.vehicleName
            )

            reportRepo.insertReport(report)
        }

    val inventoryUser = reportRepo.getAllReports().map { report ->
        report.groupBy { it.employeeId }
            .flatMap { (_, userReports) ->
                userReports
                    .groupBy { it.productId }
                    .map { (product, productReports) ->
                        InventoryItem(
                            employeeName = userReports.first().employeeName,
                            employeeSurname = userReports.first().employeeSurname,
                            productTitle = userReports.first().productTitle,
                            productDesc = userReports.first()?.productDesc,
                            productUtility = userReports.first().productUtility.displayName,
                            vehiclePlate = userReports.first().vehiclePlate,
                            vehicleName = userReports.first().vehicleName,
                            totalCount = productReports.sumOf { it.productCountChange } // somma dei deltaù
                        )
                    }.filter { it.totalCount < 0 }.map {
                        it.copy(totalCount = abs(it.totalCount))
                    }
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun filterByDatePeriod(period: DateTimePeriod?) = viewModelScope.launch {
        _selectedDatePeriod.emit(period)
    }

    fun searchReport(str: String) = viewModelScope.launch {
        _selectedDatePeriod.emit(null)
        _searchQuery.value = str
    }
}