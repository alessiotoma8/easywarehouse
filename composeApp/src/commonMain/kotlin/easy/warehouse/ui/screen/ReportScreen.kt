import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.data.openDbReportsFolder
import easy.warehouse.report.ReportEntity
import easy.warehouse.report.ReportVm
import easy.warehouse.report.getLocalDateTime
import easy.warehouse.ui.JvmDatePicker
import easy.warehouse.ui.ScreenContent
import easy.warehouse.ui.SearchBar
import easy.warehouse.ui.WAppBar
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBackClick: () -> Unit) {
    val reportVm = viewModel<ReportVm>()
    val reports by reportVm.reports.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            WAppBar("Report", onBackClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box {
            ScreenContent(innerPadding) {
                ReportsContent(
                    reportVm = reportVm, reports = reports
                )
            }

            Card(
                modifier = Modifier.width(450.dp).align(Alignment.BottomEnd).padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Report Export", style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                            ),
                            onClick = {
                                openDbReportsFolder()
                            }
                        ) {
                            Text("Apri cartella export")
                        }

                        Button(
                            onClick = {
                                reportVm.exportReport()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Esportazione di ${reports.size} report eseguita!")
                                }
                            }
                        ) {
                            Text("Esporta ${reports.size} Report")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportsContent(
    reportVm: ReportVm,
    reports: List<ReportEntity>,
) {
    val filteredReports = reports

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Report", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        ReportSearch(reportVm)
        Text(text = "Risultati trovati: (${filteredReports.size})")
        HorizontalDivider()
        LazyColumn {
            stickyHeader {
                ReportsHeader()
            }
            itemsIndexed(filteredReports) { index, item ->
                ReportRow(item, index)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ReportSearch(reportVm: ReportVm) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedDatePeriod by reportVm.selectedDatePeriod.collectAsStateWithLifecycle()

    val dateTimeLabels = mapOf(
        DateTimePeriod() to "Oggi",
        DateTimePeriod(days = 7) to "Ultimi 7 giorni",
        DateTimePeriod(months = 1) to "Ultimo mese",
        DateTimePeriod(years = 1) to "Ultimo anno"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                reportVm.searchReport(it)
            },
            modifier = Modifier.weight(1f),
            placeholder = "Cerca per nome, prodotto, dipendente ..."
        )

        var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
        var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }

        JvmDatePicker(
            selectedDate = selectedStartDate,
            onDateSelected = { selectedStartDate = it },
            label = "Data inizio"
        )

        JvmDatePicker(
            selectedDate = selectedEndDate,
            onDateSelected = { selectedEndDate = it },
            label = "Data fine"
        )

        LaunchedEffect(selectedStartDate, selectedEndDate) {
            if (selectedStartDate != null && selectedEndDate != null) {
                reportVm.filterByDateRange(
                    startDate = selectedStartDate!!.atStartOfDayIn(TimeZone.currentSystemDefault()),
                    endDate = selectedEndDate!!.atEndOfDayIn(TimeZone.currentSystemDefault())
                )
            } else {
                reportVm.clearDateRange()
            }
        }
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        FilterChip(
            selected = selectedDatePeriod == null,
            onClick = { reportVm.filterByDatePeriod(null) },
            label = {
                Text(
                    text = "Tutti".uppercase(),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        dateTimeLabels.keys.forEach { dateTimePeriod ->
            FilterChip(
                selected = selectedDatePeriod == dateTimePeriod,
                onClick = { reportVm.filterByDatePeriod(dateTimePeriod) },
                label = {
                    Text(
                        text = dateTimeLabels[dateTimePeriod]?.uppercase() ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
fun LocalDate.atEndOfDayIn(timeZone: TimeZone): Instant {
    return this.atStartOfDayIn(timeZone)
        .plus(1, DateTimeUnit.DAY, timeZone)
        .minus(1.nanoseconds)
}


@Composable
private fun ReportsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HeaderCell("Data", weight = 1f)
        HeaderCell("Ora", weight = 1f)
        HeaderCell("Dipendente", weight = 1.5f)
        HeaderCell("Prodotto", weight = 2f)
        HeaderCell("Desc prodotto", weight = 2f)
        HeaderCell("Settore prodotto", weight = 1f)
        HeaderCell("Nome veicolo", weight = 1f)
        HeaderCell("Targa veicolo", weight = 1f)
        HeaderCell("Quantità Magazzino", weight = 1f, alignRight = true)
    }
}


@Composable
private fun ReportRow(report: ReportEntity, index: Int) {
    val backgroundColor = if (index % 2 == 0) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.background
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val reportDateTime = report.getLocalDateTime()
        val formattedTime = "${reportDateTime.time.hour}:${reportDateTime.time.minute}"
        val formattedDate =
            "${reportDateTime.date.dayOfMonth}/${reportDateTime.date.monthNumber}/${reportDateTime.date.year}"

        Cell(formattedDate, weight = 1f)
        Cell(formattedTime, weight = 1f)
        Cell("${report.employeeName} ${report.employeeSurname}", weight = 1.5f)
        Cell(report.productTitle, weight = 2f)
        Cell(report.productDesc ?: "-", weight = 2f)
        Cell(report.productUtility.displayName, weight = 1f)
        Cell(report.vehicleName ?: "-", weight = 1f)
        Cell(report.vehiclePlate ?: "-", weight = 1f)

        val displayCount = if (report.productCountChange > 0)
            "+${report.productCountChange}" else report.productCountChange.toString()

        // Determine the color based on the sign of productCountChange
        val cellColor = if (report.productCountChange > 0) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }

        // Pass the color to the Cell Composable
        Cell(
            text = displayCount,
            weight = 1f,
            alignRight = true,
            color = cellColor
        )
    }
    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
}

@Composable
private fun RowScope.HeaderCell(
    text: String,
    weight: Float,
    alignRight: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontWeight = FontWeight.Bold,
        textAlign = if (alignRight) TextAlign.End else TextAlign.Start,
        style = MaterialTheme.typography.titleSmall.copy(color = color)
    )
}

@Composable
private fun RowScope.Cell(
    text: String,
    weight: Float,
    alignRight: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        textAlign = if (alignRight) TextAlign.End else TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium.copy(color = color)
    )
}



