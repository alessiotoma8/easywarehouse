import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.report.ReportEntity
import easy.warehouse.report.ReportVm
import easy.warehouse.report.getLocalDateTime
import easy.warehouse.ui.ScreenContent
import easy.warehouse.ui.SearchBar
import easy.warehouse.ui.WAppBar
import kotlinx.datetime.DateTimePeriod
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    val reportVm = viewModel<ReportVm>()
    val reports by reportVm.reports.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    val selectedDatePeriod by reportVm.selectedDatePeriod.collectAsStateWithLifecycle()

    val dateTimeLabels = mapOf(
        DateTimePeriod() to "Oggi",
        DateTimePeriod(days = 7) to "Ultimi 7 giorni",
        DateTimePeriod(months = 1) to "Ultimo mese",
        DateTimePeriod(years = 1) to "Ultimo anno"
    )

    val filteredReports = reports.filter { report ->
        val query = searchQuery.lowercase()
        report.employeeName.lowercase().contains(query) ||
                report.employeeSurname.lowercase().contains(query) ||
                report.productTitle.lowercase().contains(query) ||
                report.productDesc.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            WAppBar("Report Magazzino")
        }
    ) { innerPadding ->
        ScreenContent(innerPadding) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Cerca per nome, prodotto, dipendente ..."
            )

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


            val inventory = reportVm.generateInventory(reports)

            inventory.forEach {
                Text("${it.employeeName} ${it.employeeSurname} ha ${it.totalCount} ${it.productTitle}")
            }
            // Intestazione della tabella
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Intestazione fissata in cima
                stickyHeader {
                    ReportsHeader()
                }

                // Dati della tabella
                items(filteredReports) { report ->
                    ReportRow(report)
                }
            }
        }
    }
}

@Composable
fun ReportsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HeaderCell("Data", weight = 1f)
        HeaderCell("Dipendente", weight = 1.5f)
        HeaderCell("Prodotto", weight = 2f)
        HeaderCell("Quantità", weight = 1f, alignRight = true)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun ReportRow(report: ReportEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val reportDateTime = report.getLocalDateTime()
        val formattedTime = "${reportDateTime.time.hour}:${reportDateTime.time.minute}"
        val formattedDate =
            "${reportDateTime.date.dayOfMonth}/${reportDateTime.date.monthNumber}/${reportDateTime.date.year}"

        Cell("$formattedDate\n$formattedTime", weight = 1f)
        Cell("${report.employeeName} ${report.employeeSurname}", weight = 1.5f)
        Cell(report.productTitle, weight = 2f)
        Cell(report.productCountChange.toString(), weight = 1f, alignRight = true)
    }
    // Aggiungi un separatore per ogni riga
    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
}

@Composable
fun RowScope.HeaderCell(text: String, weight: Float, alignRight: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontWeight = FontWeight.Bold,
        textAlign = if (alignRight) TextAlign.End else TextAlign.Start,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun RowScope.Cell(text: String, weight: Float, alignRight: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        textAlign = if (alignRight) TextAlign.End else TextAlign.Start,
        style = MaterialTheme.typography.bodySmall
    )
}