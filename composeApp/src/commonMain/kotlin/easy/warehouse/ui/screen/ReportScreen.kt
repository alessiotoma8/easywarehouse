import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.report.ReportEntity
import easy.warehouse.report.ReportVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    val reports by viewModel<ReportVm>().reports.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredReports = reports.filter { report ->
        val query = searchQuery.text.lowercase()
        report.employeeName.lowercase().contains(query) ||
                report.employeeSurname.lowercase().contains(query) ||
                report.productTitle.lowercase().contains(query) ||
                report.date.lowercase().contains(query) ||
                report.time.lowercase().contains(query) ||
                report.productDesc.lowercase().contains(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Magazzino") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            // Campo di ricerca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cerca per nome, prodotto, data...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ricerca") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Intestazione della tabella
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
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

@Composable
fun ReportRow(report: ReportEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Cell("${report.date}\n${report.time}", weight = 1f)
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