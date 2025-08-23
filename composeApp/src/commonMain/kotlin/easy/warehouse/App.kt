package easy.warehouse

import LoginScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import easy.warehouse.product.Utility
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

private val accountManager = AccountManager()

val DarkGreen = Color(0xFF1B5E20)
val DarkRed = Color(0xFFB71C1C)

@Composable
@Preview
fun App() {
    AppTheme {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showLoginScreen by remember { mutableStateOf(false) }
            var isAdmin by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    showLoginScreen = !showLoginScreen
                    if (!showLoginScreen) {
                        isAdmin = false
                    }
                },
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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
                title = { Text("Magazzino", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
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
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Utente",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
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
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                            DestinationSelection(
                                vehicles = vehicles,
                                selectedVehicle = selectedVehicle,
                                onVehicleSelected = { selectedVehicle = it }
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    Text(
                        text = "Gestione Prodotti",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item(span = { GridItemSpan(this.maxLineSpan) }) {
                    val searchQuery by productVm.searchQuery.collectAsStateWithLifecycle("")
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { productVm.updateSearch(it) }
                        )
                        ProductFilterChips(productVm)
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

@Composable
fun ProductItem(product: ProductEntity, productVm: ProductVm) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        product.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        product.content,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        product.utility.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Totale", color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = product.count.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { productVm.decreaseCount(product.id) },
                    enabled = product.count > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Prendi",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prendi")
                }

                Button(
                    onClick = { productVm.increaseCount(product.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Lascia",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lascia")
                }
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
        modifier = modifier.width(350.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Modifiche in Sospeso",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            pendingChanges.values.forEach { change ->
                val action = if (change.delta > 0) "Lasciato" else "Preso"
                val delta = if (change.delta > 0) change.delta else -change.delta
                val color = if (change.delta > 0) DarkRed else DarkGreen

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$action $delta: ${change.title}",
                        color = color,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (action == "Preso") {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = DarkRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
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
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancella")
                }
                Button(
                    onClick = onSave,
                    enabled = isSaveEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
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
    GenericExposedDropdownMenu(
        items = employees,
        selectedItem = selectedEmployee,
        onItemSelected = onEmployeeSelected,
        itemText = { "${it.name} ${it.surname}" },
        label = "Seleziona Utente"
    )
}

@Composable
fun DestinationSelection(
    vehicles: List<VehicleDestinationEntity>,
    selectedVehicle: VehicleDestinationEntity?,
    onVehicleSelected: (VehicleDestinationEntity) -> Unit,
) {
    GenericExposedDropdownMenu(
        items = vehicles,
        selectedItem = selectedVehicle,
        onItemSelected = onVehicleSelected,
        itemText = { it.vehicleName + " - (${it.vehiclePlate}) " },
        label = "Seleziona Veicolo"
    )
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
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { itemText(it).contains(searchQuery, ignoreCase = true) }
    }

    val searchFocusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.clip(MaterialTheme.shapes.extraSmall),
    ) {
        TextField(
            readOnly = true,
            value = selectedItem?.let(itemText) ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = ""
            }
        ) {
            LaunchedEffect(expanded) {
                if (expanded) {
                    searchFocusRequester.requestFocus()
                }
            }

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cerca...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .focusRequester(searchFocusRequester)
            )

            filteredItems.forEachIndexed { _, item ->
                DropdownMenuItem(
                    text = { Text(itemText(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                        searchQuery = ""
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
    placeholder: String = "Cerca per titolo, contenuto o ID...",
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFilterChips(
    productVm: ProductVm,
    modifier: Modifier = Modifier,
) {
    val selectedUtilities by productVm.selectedUtility.collectAsStateWithLifecycle()
    val allUtilities = Utility.entries

    FlowRow(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        FilterChip(
            selected = selectedUtilities == null,
            onClick = { productVm.toggleUtilityFilter(null) },
            label = {
                Text(text = "Tutti".uppercase(), style = MaterialTheme.typography.titleLarge)
            },
            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        allUtilities.forEach { utility ->
            val isSelected = selectedUtilities == utility
            FilterChip(
                selected = isSelected,
                onClick = { productVm.toggleUtilityFilter(utility) },
                label = {
                    Text(
                        text = utility.displayName.uppercase(),
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
