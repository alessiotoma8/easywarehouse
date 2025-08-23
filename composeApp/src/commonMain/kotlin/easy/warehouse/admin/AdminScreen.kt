package easy.warehouse.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.destination.VehicleVm
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.employee.EmployeeVm
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AdminScreen() {
    var selectedTabActionIndex by remember { mutableStateOf(0) }
    val actions = listOf("Aggiungi", "Rimuovi")

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Utenti", "Veicoli", "Prodotti")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pannello di Amministrazione") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Tab per l'azione (Aggiungi/Rimuovi)
            TabRow(selectedTabIndex = selectedTabActionIndex) {
                actions.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabActionIndex == index,
                        onClick = { selectedTabActionIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            // Tab per la categoria (Utenti/Veicoli/Prodotti)
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

            // Mostra la sezione corretta
            when (selectedTabActionIndex) {
                0 -> { // Sezione Aggiungi
                    when (selectedTabIndex) {
                        0 -> UserAddSection()
                        1 -> VehicleAddSection()
                        2 -> ProductAddSection()
                    }
                }
                1 -> { // Sezione Rimuovi
                    when (selectedTabIndex) {
                        0 -> UserRemoveSection()
                        1 -> VehicleRemoveSection()
                        2 -> ProductRemoveSection()
                    }
                }
            }
        }
    }
}

// Sezioni di Aggiunta (codice precedente, rinominato per chiarezza)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddSection() {
    val employeeVm = viewModel<EmployeeVm>()
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Utente", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Nome") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = surname, onValueChange = { surname = it },
            label = { Text("Cognome") }, modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (name.isNotBlank() && surname.isNotBlank()) {
                    employeeVm.addEmployee(name, surname)
                    name = ""
                    surname = ""
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
fun VehicleAddSection() {
    val vehicleVm = viewModel<VehicleVm>()
    var vehiclePlate by remember { mutableStateOf("") }
    var vehicleName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Veicolo", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = vehiclePlate, onValueChange = { vehiclePlate = it },
            label = { Text("Targa Veicolo") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vehicleName, onValueChange = { vehicleName = it },
            label = { Text("Nome Veicolo") }, modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (vehiclePlate.isNotBlank() && vehicleName.isNotBlank()) {
                    vehicleVm.addVehicle(vehiclePlate, vehicleName)
                    vehiclePlate = ""
                    vehicleName = ""
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
fun ProductAddSection() {
    val productVm = viewModel<ProductVm>()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Prodotto", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Titolo Prodotto") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = content, onValueChange = { content = it },
            label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = count, onValueChange = { count = it },
            label = { Text("Quantità") }, modifier = Modifier.fillMaxWidth()
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
                }
            },
            enabled = title.isNotBlank() && content.isNotBlank() && count.toIntOrNull() != null
        ) {
            Text("Aggiungi Prodotto")
        }
    }
}

// Sezioni di Rimozione (NUOVO CODICE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRemoveSection() {
    val employeeVm = viewModel<EmployeeVm>()
    val employees by employeeVm.employees.collectAsStateWithLifecycle(emptyList())
    var selectedEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }

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
            label = "Seleziona Utente"
        )
        Button(
            onClick = {
                selectedEmployee?.let { employee ->
                    employeeVm.removeEmployee(employee.id)
                    selectedEmployee = null
                }
            },
            enabled = selectedEmployee != null
        ) {
            Text("Rimuovi Utente")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRemoveSection() {
    val vehicleVm = viewModel<VehicleVm>()
    val vehicles by vehicleVm.vehicles.collectAsStateWithLifecycle(emptyList())
    var selectedVehicle by remember { mutableStateOf<VehicleDestinationEntity?>(null) }

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
            label = "Seleziona Veicolo"
        )
        Button(
            onClick = {
                selectedVehicle?.let { vehicle ->
                    vehicleVm.removeVehicle(vehicle.vehiclePlate)
                    selectedVehicle = null
                }
            },
            enabled = selectedVehicle != null
        ) {
            Text("Rimuovi Veicolo")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRemoveSection() {
    val productVm = viewModel<ProductVm>()
    val products by productVm.displayProducts.collectAsStateWithLifecycle(emptyList())
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }

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
            label = "Seleziona Prodotto"
        )
        Button(
            onClick = {
                selectedProduct?.let { product ->
                    productVm.removeProduct(product.id)
                    selectedProduct = null
                }
            },
            enabled = selectedProduct != null
        ) {
            Text("Rimuovi Prodotto")
        }
    }
}

// Funzione generica per il dropdown (codice esistente)
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