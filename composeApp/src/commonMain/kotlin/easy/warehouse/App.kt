package easy.warehouse

import LoginScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.ui.theme.AppTheme
import easy.warehouse.admin.AdminScreen
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.destination.VehicleVm
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.employee.EmployeeVm
import easy.warehouse.product.PendingChange
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

private val accountManager = AccountManager()
private val DarkGreen = Color(0xFF006400)
private val DarkRed = Color(0xFF8B0000)

@Composable
@Preview
fun App() {
    AppTheme(
        //xshapes = AppTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showLoginScreen by remember { mutableStateOf(false) }
            var isAdmin by remember { mutableStateOf(false) }
            Button(onClick = {
                showLoginScreen = !showLoginScreen
                if (!showLoginScreen) {
                    isAdmin = false
                }
            }) {
                if (isAdmin) {
                    Text("Esci")
                } else {
                    Text("Accedi")
                }
            }
            if (!showLoginScreen) {
                WarehouseScreen()
            } else if (!isAdmin) {
                LoginScreen { us, pwd ->
                    isAdmin = accountManager.login(us, pwd)
                }
            } else {
                AdminScreen()
            }
        }
    }
}

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
        topBar = {
            TopAppBar(
                title = { Text("Magazzino") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Contenuto della griglia
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 400.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentPadding = PaddingValues(bottom = products.size / 3 * 100.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Selezioni Utente e Destinazione come elementi fissi
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Utente",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            UserSelection(
                                employees = employees,
                                selectedEmployee = selectedEmployee,
                                onEmployeeSelected = { selectedEmployee = it }
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Destinazione",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DestinationSelection(
                                vehicles = vehicles,
                                selectedVehicle = selectedVehicle,
                                onVehicleSelected = { selectedVehicle = it }
                            )
                        }
                    }
                }

                // Titolo "Prodotti" come elemento fisso
                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Text(
                        text = "Prendi o Lascia i prodotti dal magazzino!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    val searchQuery by productVm.searchQuery.collectAsStateWithLifecycle("")
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { productVm.updateSearch(it) }
                    )
                }

                // Griglia di prodotti
                items(products) { product ->
                    Card(
                        modifier = Modifier.widthIn(min = 200.dp, max = 400.dp)
                    ) {
                        ProductItem(product, productVm)
                    }
                }
            }

            // Card delle modifiche posizionata come overlay
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

@Composable
fun ProductItem(product: ProductEntity, productVm: ProductVm) {
    Column {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(product.title, style = MaterialTheme.typography.titleMedium)
                Text(product.content, style = MaterialTheme.typography.bodyMedium)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Tot")
                Text(
                    text = product.count.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }


        HorizontalDivider()


        Row(
            modifier = Modifier.padding(16.dp).align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                onClick = { productVm.decreaseCount(product.id) },
                enabled = product.count > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "Prendi")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Prendi")
            }

            Button(
                onClick = { productVm.increaseCount(product.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkRed,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.RemoveShoppingCart, contentDescription = "Lascia")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lascia")
            }
        }

    }
}

@Composable
fun ChangesSummary(
    pendingChanges: Map<Long, PendingChange>,
    onSave: () -> Unit,
    onClear: () -> Unit,
    isSaveEnabled: Boolean,
    isClearEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.width(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Modifiche da Applicare",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            pendingChanges.values.forEach { change ->
                val action = if (change.delta > 0) "Hai lasciato" else "Hai preso"
                val delta = if (change.delta > 0) change.delta else -change.delta
                val color = if (change.delta > 0) Color.Red else Color.Green

                Text(
                    text = "$action $delta: ${change.title}",
                    color = color,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClear,
                    enabled = isClearEnabled,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Cancella")
                }
                Button(
                    onClick = onSave,
                    enabled = isSaveEnabled
                ) {
                    Text("Salva")
                }
            }
        }
    }
}

@Composable
fun UserSelection(
    employees: List<EmployeeEntity>,
    selectedEmployee: EmployeeEntity?,
    onEmployeeSelected: (EmployeeEntity) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        GenericExposedDropdownMenu(
            items = employees,
            selectedItem = selectedEmployee,
            onItemSelected = onEmployeeSelected,
            itemText = { "${it.name} ${it.surname}" },
            label = "Seleziona Utente"
        )
    }
}

@Composable
fun DestinationSelection(
    vehicles: List<VehicleDestinationEntity>,
    selectedVehicle: VehicleDestinationEntity?,
    onVehicleSelected: (VehicleDestinationEntity) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        GenericExposedDropdownMenu(
            items = vehicles,
            selectedItem = selectedVehicle,
            onItemSelected = onVehicleSelected,
            itemText = { it.vehicleName },
            label = "Seleziona Veicolo"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericExposedDropdownMenu(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemText: (T) -> String,
    label: String = "Seleziona Elemento",
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.clip(MaterialTheme.shapes.extraSmall),
    ) {
        TextField(
            readOnly = true,
            value = selectedItem?.let(itemText) ?: label,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemText(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cerca...",
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(12.dp)
    )
}
