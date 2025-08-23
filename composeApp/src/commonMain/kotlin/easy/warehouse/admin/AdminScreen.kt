package easy.warehouse.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.destination.VehicleVm
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.employee.EmployeeVm
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

data class TabAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AdminScreen() {
    var selectedTabActionIndex by remember { mutableStateOf(0) }
    val actions = listOf(
        TabAction("Aggiungi", Icons.Default.AddCircle, Color(0xFF4CAF50)),
        TabAction("Rimuovi", Icons.Default.RemoveCircle, Color(0xFFF44336))
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Utenti", "Veicoli", "Prodotti")

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pannello di Amministrazione") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabActionIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                actions.forEachIndexed { index, tabAction ->
                    Tab(
                        selected = selectedTabActionIndex == index,
                        onClick = { selectedTabActionIndex = index },
                        selectedContentColor = tabAction.color,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.background(tabAction.color.copy(alpha = 0.2f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(imageVector = tabAction.icon, contentDescription = tabAction.title)
                            Text(text = tabAction.title)
                        }
                    }
                }
            }
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabActionIndex) {
                0 -> {
                    when (selectedTabIndex) {
                        0 -> UserAddSection(snackbarHostState)
                        1 -> VehicleAddSection(snackbarHostState)
                        2 -> ProductAddSection(snackbarHostState)
                    }
                }
                1 -> {
                    when (selectedTabIndex) {
                        0 -> UserRemoveSection(snackbarHostState)
                        1 -> VehicleRemoveSection(snackbarHostState)
                        2 -> ProductRemoveSection(snackbarHostState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddSection(snackbarHostState: SnackbarHostState) {
    val employeeVm = viewModel<EmployeeVm>()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Utente", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Cognome") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        Button(
            onClick = {
                if (name.isNotBlank() && surname.isNotBlank()) {
                    employeeVm.addEmployee(name, surname)
                    name = ""
                    surname = ""
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Utente aggiunto con successo!")
                    }
                }
            },
            enabled = name.isNotBlank() && surname.isNotBlank()
        ) {
            Text("Aggiungi Utente")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleAddSection(snackbarHostState: SnackbarHostState) {
    val vehicleVm = viewModel<VehicleVm>()
    var vehiclePlate by remember { mutableStateOf("") }
    var vehicleName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Veicolo", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = vehiclePlate,
            onValueChange = { vehiclePlate = it },
            label = { Text("Targa Veicolo") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = vehicleName,
            onValueChange = { vehicleName = it },
            label = { Text("Nome Veicolo") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        Button(
            onClick = {
                if (vehiclePlate.isNotBlank() && vehicleName.isNotBlank()) {
                    vehicleVm.addVehicle(vehiclePlate, vehicleName)
                    vehiclePlate = ""
                    vehicleName = ""
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Veicolo aggiunto con successo!")
                    }
                }
            },
            enabled = vehiclePlate.isNotBlank() && vehicleName.isNotBlank()
        ) {
            Text("Aggiungi Veicolo")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddSection(snackbarHostState: SnackbarHostState) {
    val productVm = viewModel<ProductVm>()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Prodotto", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titolo Prodotto") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Descrizione") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = count,
            onValueChange = { count = it },
            label = { Text("Quantità") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank() && count.isNotBlank()) {
                    productVm.insertProduct(
                        ProductEntity(
                            title = title, content = content, count = count.toIntOrNull() ?: 0
                        )
                    )
                    title = ""
                    content = ""
                    count = ""
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Prodotto aggiunto con successo!")
                    }
                }
            },
            enabled = title.isNotBlank() && content.isNotBlank() && count.toIntOrNull() != null
        ) {
            Text("Aggiungi Prodotto")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRemoveSection(snackbarHostState: SnackbarHostState) {
    val employeeVm = viewModel<EmployeeVm>()
    val employees by employeeVm.employees.collectAsStateWithLifecycle(emptyList())
    var selectedEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Rimuovi un Utente", style = MaterialTheme.typography.titleLarge)
        GenericExposedDropdownMenu(
            items = employees,
            selectedItem = selectedEmployee,
            onItemSelected = { selectedEmployee = it },
            itemText = { "${it.name} ${it.surname}" },
            label = "Seleziona Utente",
            modifier = Modifier.widthIn(max = 400.dp)
        )
        Button(
            onClick = {
                selectedEmployee?.let { employee ->
                    employeeVm.removeEmployee(employee.id)
                    selectedEmployee = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Utente rimosso con successo!")
                    }
                }
            },
            enabled = selectedEmployee != null,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text("Rimuovi Utente")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRemoveSection(snackbarHostState: SnackbarHostState) {
    val vehicleVm = viewModel<VehicleVm>()
    val vehicles by vehicleVm.vehicles.collectAsStateWithLifecycle(emptyList())
    var selectedVehicle by remember { mutableStateOf<VehicleDestinationEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Rimuovi un Veicolo", style = MaterialTheme.typography.titleLarge)
        GenericExposedDropdownMenu(
            items = vehicles,
            selectedItem = selectedVehicle,
            onItemSelected = { selectedVehicle = it },
            itemText = { it.vehicleName },
            label = "Seleziona Veicolo",
            modifier = Modifier.widthIn(max = 400.dp)
        )
        Button(
            onClick = {
                selectedVehicle?.let { vehicle ->
                    vehicleVm.removeVehicle(vehicle.vehiclePlate)
                    selectedVehicle = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Veicolo rimosso con successo!")
                    }
                }
            },
            enabled = selectedVehicle != null,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text("Rimuovi Veicolo")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRemoveSection(snackbarHostState: SnackbarHostState) {
    val productVm = viewModel<ProductVm>()
    val products by productVm.displayProducts.collectAsStateWithLifecycle(emptyList())
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Rimuovi un Prodotto", style = MaterialTheme.typography.titleLarge)
        GenericExposedDropdownMenu(
            items = products,
            selectedItem = selectedProduct,
            onItemSelected = { selectedProduct = it },
            itemText = { it.title },
            label = "Seleziona Prodotto",
            modifier = Modifier.widthIn(max = 400.dp)
        )
        Button(
            onClick = {
                selectedProduct?.let { product ->
                    productVm.removeProduct(product.id)
                    selectedProduct = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Prodotto rimosso con successo!")
                    }
                }
            },
            enabled = selectedProduct != null,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text("Rimuovi Prodotto")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericExposedDropdownMenu(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemText: (T) -> String,
    label: String = "Seleziona Item",
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
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