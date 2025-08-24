package easy.warehouse.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.components.ChangesSummary
import easy.warehouse.components.DestinationSelection
import easy.warehouse.components.ProductFilterChips
import easy.warehouse.components.ProductItem
import easy.warehouse.components.SearchBar
import easy.warehouse.components.WAppBar
import easy.warehouse.components.UserSelection
import easy.warehouse.destination.VehicleVm
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.employee.EmployeeVm
import easy.warehouse.product.ProductVm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen() {
    val productVm = viewModel<ProductVm>()
    val products by productVm.displayProducts.collectAsStateWithLifecycle(emptyList())
    val pendingChanges by productVm.pendingChanges.collectAsStateWithLifecycle(emptyMap())

    val employeeVm = viewModel<EmployeeVm>()
    val employees by employeeVm.employees.collectAsStateWithLifecycle(emptyList())
    var selectedEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }

    val vehicleVm = viewModel<VehicleVm>()
    val vehicles by vehicleVm.vehicles.collectAsStateWithLifecycle(emptyList())
    var selectedVehicle by remember { mutableStateOf<VehicleDestinationEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { WAppBar("Magazzino") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserSelection(
                            employees = employees,
                            selectedEmployee = selectedEmployee,
                            onEmployeeSelected = { selectedEmployee = it },
                            modifier = Modifier.weight(1f)
                        )
                        DestinationSelection(
                            vehicles = vehicles,
                            selectedVehicle = selectedVehicle,
                            onVehicleSelected = { selectedVehicle = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val searchQuery by productVm.searchQuery.collectAsStateWithLifecycle("")
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { productVm.updateSearch(it) }
                        )
                        ProductFilterChips(productVm)
                        Text(text = "Prodotti trovati: (${products.size})")
                    }
                }

                items(products) { product ->
                    ProductItem(product, productVm)
                }
            }

            if (pendingChanges.isNotEmpty()) {
                ChangesSummary(
                    pendingChanges = pendingChanges,
                    onSave = {
                        productVm.saveChanges()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Magazzino aggiornato con successo")
                        }
                    },
                    onClear = { productVm.clearChanges() },
                    isSaveEnabled = selectedEmployee != null && selectedVehicle != null,
                    isClearEnabled = pendingChanges.isNotEmpty(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}