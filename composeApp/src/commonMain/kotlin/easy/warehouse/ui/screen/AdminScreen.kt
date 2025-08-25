package easy.warehouse.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import easy.warehouse.product.Utility
import easy.warehouse.ui.WAppBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

// Definizione del modello di dato per un campo di input generico
data class Field<T>(
    val label: String,
    val state: MutableState<T>,
    val isRequired: Boolean = false,
    val isEnabled: Boolean = true,
)

data class TabAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
)

val Green2 = Color(0xFF4CAF50)
val Red2 = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AdminScreen() {
    var selectedTabActionIndex by remember { mutableStateOf(0) }
    val actions = listOf(
        TabAction("Aggiungi", Icons.Default.AddCircle, Green2),
        TabAction("Rimuovi/ Modifica", Icons.Default.RemoveCircle, Red2)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Utenti", "Veicoli", "Prodotti")

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            WAppBar("Pannello di Amministrazione")
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
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

            Box(
                modifier = Modifier.weight(1f)
            ) {
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
        }
    }
}

// -------------------------------------------------------------------------------------------------
// COMPONENTE GENERICO BASE PER L'AGGIUNTA DI ELEMENTI
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseAddTab(
    title: String,
    fields: List<Field<String>>,
    saveAction: () -> Unit,
    saveButtonText: String,
    snackbarHostState: SnackbarHostState,
    snackbarMessage: String,
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        fields.forEach { field ->
            OutlinedTextField(
                value = field.state.value,
                onValueChange = { field.state.value = it },
                label = { Text(field.label) },
                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
            )
        }
        Button(
            onClick = {
                val allFieldsFilled = fields.all { !it.isRequired || it.state.value.isNotBlank() }
                if (allFieldsFilled) {
                    saveAction()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(snackbarMessage)
                    }
                }
            },
            enabled = fields.all { !it.isRequired || it.state.value.isNotBlank() }
        ) {
            Text(saveButtonText)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// RIFATTORIZZAZIONE DELLE SEZIONI ESISTENTI USANDO LA COMPOSABLE BASE
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddSection(snackbarHostState: SnackbarHostState) {
    val employeeVm = viewModel<EmployeeVm>()
    val nameState = remember { mutableStateOf("") }
    val surnameState = remember { mutableStateOf("") }

    val fields = listOf(
        Field("Nome", nameState, isRequired = true),
        Field("Cognome", surnameState, isRequired = true)
    )

    BaseAddTab(
        title = "Inserisci un nuovo Utente",
        fields = fields,
        saveAction = {
            employeeVm.addEmployee(nameState.value, surnameState.value)
            nameState.value = ""
            surnameState.value = ""
        },
        saveButtonText = "Aggiungi Utente",
        snackbarHostState = snackbarHostState,
        snackbarMessage = "Utente aggiunto con successo!"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleAddSection(snackbarHostState: SnackbarHostState) {
    val vehicleVm = viewModel<VehicleVm>()
    val vehiclePlateState = remember { mutableStateOf("") }
    val vehicleNameState = remember { mutableStateOf("") }

    val fields = listOf(
        Field("Targa Veicolo", vehiclePlateState, isRequired = true),
        Field("Nome Veicolo", vehicleNameState, isRequired = true)
    )

    BaseAddTab(
        title = "Inserisci un nuovo Veicolo",
        fields = fields,
        saveAction = {
            vehicleVm.addVehicle(vehiclePlateState.value, vehicleNameState.value)
            vehiclePlateState.value = ""
            vehicleNameState.value = ""
        },
        saveButtonText = "Aggiungi Veicolo",
        snackbarHostState = snackbarHostState,
        snackbarMessage = "Veicolo aggiunto con successo!"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddSection(snackbarHostState: SnackbarHostState) {
    val productVm = viewModel<ProductVm>()
    val titleState = remember { mutableStateOf("") }
    val contentState = remember { mutableStateOf("") }
    val countState = remember { mutableStateOf("") }
    var utility by remember { mutableStateOf<Utility?>(null) }
    val coroutineScope = rememberCoroutineScope()


    // Purtroppo per il dropdown la logica è più complessa e non si può generalizzare con i TextField
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Inserisci un nuovo Prodotto", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = { Text("Titolo Prodotto") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )
        OutlinedTextField(
            value = contentState.value,
            onValueChange = { contentState.value = it },
            label = { Text("Descrizione") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )


        OutlinedTextField(
            value = countState.value,
            onValueChange = {
                countState.value = it
            },
            label = { Text("Quantità") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )

        GenericExposedDropdownMenu(
            items = Utility.values().toList(),
            selectedItem = utility,
            onItemSelected = { utility = it },
            itemText = { "${it.displayName} " },
            label = "Seleziona Settore",
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
        )


        Button(
            onClick = {
                if (titleState.value.isNotBlank() && contentState.value.isNotBlank() && countState.value.isNotBlank() && utility != null) {
                    productVm.insertProduct(
                        ProductEntity(
                            title = titleState.value,
                            content = contentState.value,
                            count = countState.value.toIntOrNull() ?: 0,
                            utility = utility ?: Utility.ALTRI
                        )
                    )
                    titleState.value = ""
                    contentState.value = ""
                    countState.value = ""
                    utility = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Prodotto aggiunto con successo!")
                    }
                }
            },
            enabled = titleState.value.isNotBlank() && contentState.value.isNotBlank() && countState.value.toIntOrNull() != null && utility != null,
        ) {
            Text("Aggiungi Prodotto")
        }
    }
}
// -------------------------------------------------------------------------------------------------
// SEZIONI DI RIMOZIONE/MODIFICA (NON MODIFICATE)
// -------------------------------------------------------------------------------------------------

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