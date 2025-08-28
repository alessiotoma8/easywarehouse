package easy.warehouse.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import easy.warehouse.ui.GenericExposedDropdownMenu
import easy.warehouse.ui.ScreenContent
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
fun AdminScreen(onLogoutClick: () -> Unit = {}, onReportClick: () -> Unit = {}) {
    var selectedTabActionIndex by remember { mutableStateOf(0) }
    val actions = listOf(
        TabAction("Aggiungi", Icons.Default.AddCircle, Green2),
        TabAction("Rimuovi/ Modifica", Icons.Default.RemoveCircle, Red2)
    )

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Utenti", "Veicoli", "Prodotti")

    val snackbarHostState = remember { SnackbarHostState() }

    // Stati per la gestione della modifica
    var selectedEmployeeForEdit by remember { mutableStateOf<EmployeeEntity?>(null) }
    var selectedVehicleForEdit by remember { mutableStateOf<VehicleDestinationEntity?>(null) }
    var selectedProductForEdit by remember { mutableStateOf<ProductEntity?>(null) }


    Scaffold(
        topBar = {
            WAppBar("Pannello di Amministrazione") {
                Button(
                    onClick = onReportClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                ) {
                    Text("Report")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                ) {
                    Text("Esci")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        ScreenContent(innerPadding) {
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
                            0 -> UserRemoveSection(
                                snackbarHostState,
                                selectedEmployeeForEdit,
                                onEmployeeEdit = { selectedEmployeeForEdit = it },
                                onEditComplete = { selectedEmployeeForEdit = null }
                            )
                            1 -> VehicleRemoveSection(
                                snackbarHostState,
                                selectedVehicleForEdit,
                                onVehicleEdit = { selectedVehicleForEdit = it },
                                onEditComplete = { selectedVehicleForEdit = null }
                            )
                            2 -> ProductRemoveSection(
                                snackbarHostState,
                                selectedProductForEdit,
                                onProductEdit = { selectedProductForEdit = it },
                                onEditComplete = { selectedProductForEdit = null }
                            )
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
// COMPONENTE GENERICO BASE PER L'AGGIUNTA E LA MODIFICA DI ELEMENTI
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
                modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                enabled = field.isEnabled
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
// SEZIONI DI AGGIUNTA E MODIFICA DEGLI UTENTI
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddSection(
    snackbarHostState: SnackbarHostState,
    employeeToEdit: EmployeeEntity? = null,
    onEditComplete: () -> Unit = {}
) {
    val employeeVm = viewModel<EmployeeVm>()
    val nameState = remember { mutableStateOf(employeeToEdit?.name ?: "") }
    val surnameState = remember { mutableStateOf(employeeToEdit?.surname ?: "") }

    val fields = listOf(
        Field("Nome", nameState, isRequired = true),
        Field("Cognome", surnameState, isRequired = true)
    )

    BaseAddTab(
        title = if (employeeToEdit == null) "Inserisci un nuovo Utente" else "Modifica Utente",
        fields = fields,
        saveAction = {
            if (employeeToEdit == null) {
                employeeVm.addEmployee(nameState.value, surnameState.value)
            } else {
                // TODO: Implementare la logica di aggiornamento (update)
                // employeeVm.updateEmployee(employeeToEdit.id, nameState.value, surnameState.value)
            }
            nameState.value = ""
            surnameState.value = ""
            onEditComplete()
        },
        saveButtonText = if (employeeToEdit == null) "Aggiungi Utente" else "Salva Modifiche",
        snackbarHostState = snackbarHostState,
        snackbarMessage = if (employeeToEdit == null) "Utente aggiunto con successo!" else "Utente modificato con successo!"
    )
}

// -------------------------------------------------------------------------------------------------
// SEZIONI DI AGGIUNTA E MODIFICA DEI VEICOLI
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleAddSection(
    snackbarHostState: SnackbarHostState,
    vehicleToEdit: VehicleDestinationEntity? = null,
    onEditComplete: () -> Unit = {}
) {
    val vehicleVm = viewModel<VehicleVm>()
    val vehiclePlateState = remember { mutableStateOf(vehicleToEdit?.vehiclePlate ?: "") }
    val vehicleNameState = remember { mutableStateOf(vehicleToEdit?.vehicleName ?: "") }

    val fields = listOf(
        Field("Targa Veicolo", vehiclePlateState, isRequired = true, isEnabled = vehicleToEdit == null),
        Field("Nome Veicolo", vehicleNameState, isRequired = true)
    )

    BaseAddTab(
        title = if (vehicleToEdit == null) "Inserisci un nuovo Veicolo" else "Modifica Veicolo",
        fields = fields,
        saveAction = {
            if (vehicleToEdit == null) {
                vehicleVm.addVehicle(vehiclePlateState.value, vehicleNameState.value)
            } else {
                // TODO: Implementare la logica di aggiornamento (update)
                // vehicleVm.updateVehicle(vehicleToEdit.vehiclePlate, vehicleNameState.value)
            }
            vehiclePlateState.value = ""
            vehicleNameState.value = ""
            onEditComplete()
        },
        saveButtonText = if (vehicleToEdit == null) "Aggiungi Veicolo" else "Salva Modifiche",
        snackbarHostState = snackbarHostState,
        snackbarMessage = if (vehicleToEdit == null) "Veicolo aggiunto con successo!" else "Veicolo modificato con successo!"
    )
}

// -------------------------------------------------------------------------------------------------
// SEZIONI DI AGGIUNTA E MODIFICA DEI PRODOTTI
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddSection(
    snackbarHostState: SnackbarHostState,
    productToEdit: ProductEntity? = null,
    onEditComplete: () -> Unit = {}
) {
    val productVm = viewModel<ProductVm>()
    val titleState = remember { mutableStateOf(productToEdit?.title ?: "") }
    val contentState = remember { mutableStateOf(productToEdit?.content ?: "") }
    val countState = remember { mutableStateOf(productToEdit?.count?.toString() ?: "") }
    var utility by remember { mutableStateOf(productToEdit?.utility) }
    val coroutineScope = rememberCoroutineScope()


    // Purtroppo per il dropdown la logica è più complessa e non si può generalizzare con i TextField
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (productToEdit == null) "Inserisci un nuovo Prodotto" else "Modifica Prodotto", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            label = { Text("Titolo Prodotto") },
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
            enabled = productToEdit == null
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
                if (titleState.value.isNotBlank() && countState.value.isNotBlank() && utility != null) {
                    if (productToEdit == null) {
                        productVm.insertProduct(
                            ProductEntity(
                                title = titleState.value,
                                content = contentState.value,
                                count = countState.value.toIntOrNull() ?: 0,
                                utility = utility ?: Utility.ALTRI
                            )
                        )
                    } else {
                        // TODO: Implementare la logica di aggiornamento (update)
                        // productVm.updateProduct(
                        //     productToEdit.id,
                        //     content = contentState.value,
                        //     count = countState.value.toIntOrNull() ?: 0,
                        //     utility = utility ?: Utility.ALTRI
                        // )
                    }
                    titleState.value = ""
                    contentState.value = ""
                    countState.value = ""
                    utility = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(if (productToEdit == null) "Prodotto aggiunto con successo!" else "Prodotto modificato con successo!")
                    }
                    onEditComplete()
                }
            },
            enabled = titleState.value.isNotBlank() && countState.value.toIntOrNull() != null && utility != null,
        ) {
            Text(if (productToEdit == null) "Aggiungi Prodotto" else "Salva Modifiche")
        }
    }
}
// -------------------------------------------------------------------------------------------------
// SEZIONI DI RIMOZIONE/MODIFICA (MODIFICATE)
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRemoveSection(
    snackbarHostState: SnackbarHostState,
    selectedEmployeeForEdit: EmployeeEntity?,
    onEmployeeEdit: (EmployeeEntity?) -> Unit,
    onEditComplete: () -> Unit
) {
    val employeeVm = viewModel<EmployeeVm>()
    val employees by employeeVm.employees.collectAsStateWithLifecycle(emptyList())
    var selectedEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (selectedEmployeeForEdit != null) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserAddSection(snackbarHostState, selectedEmployeeForEdit, onEditComplete)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = onEditComplete
            ) {
                Text("Annulla Modifica")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rimuovi o Modifica un Utente", style = MaterialTheme.typography.titleLarge)
            GenericExposedDropdownMenu(
                items = employees,
                selectedItem = selectedEmployee,
                onItemSelected = { selectedEmployee = it },
                itemText = { "${it.name} ${it.surname}" },
                label = "Seleziona Utente",
                modifier = Modifier.widthIn(max = 400.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
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
                ) {
                    Text("Rimuovi Utente")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    onClick = {
                        onEmployeeEdit(selectedEmployee)
                    },
                    enabled = selectedEmployee != null,
                ) {
                    Text("Modifica Utente")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRemoveSection(
    snackbarHostState: SnackbarHostState,
    selectedVehicleForEdit: VehicleDestinationEntity?,
    onVehicleEdit: (VehicleDestinationEntity?) -> Unit,
    onEditComplete: () -> Unit
) {
    val vehicleVm = viewModel<VehicleVm>()
    val vehicles by vehicleVm.vehicles.collectAsStateWithLifecycle(emptyList())
    var selectedVehicle by remember { mutableStateOf<VehicleDestinationEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (selectedVehicleForEdit != null) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VehicleAddSection(snackbarHostState, selectedVehicleForEdit, onEditComplete)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onEditComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
            ) {
                Text("Annulla Modifica")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rimuovi o Modifica un Veicolo", style = MaterialTheme.typography.titleLarge)
            GenericExposedDropdownMenu(
                items = vehicles,
                selectedItem = selectedVehicle,
                onItemSelected = { selectedVehicle = it },
                itemText = { it.vehicleName },
                label = "Seleziona Veicolo",
                modifier = Modifier.widthIn(max = 400.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
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
                ) {
                    Text("Rimuovi Veicolo")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    onClick = {
                        onVehicleEdit(selectedVehicle)
                    },
                    enabled = selectedVehicle != null,
                ) {
                    Text("Modifica Veicolo")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRemoveSection(
    snackbarHostState: SnackbarHostState,
    selectedProductForEdit: ProductEntity?,
    onProductEdit: (ProductEntity?) -> Unit,
    onEditComplete: () -> Unit
) {
    val productVm = viewModel<ProductVm>()
    val products by productVm.displayProducts.collectAsStateWithLifecycle(emptyList())
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (selectedProductForEdit != null) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProductAddSection(snackbarHostState, selectedProductForEdit, onEditComplete)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onEditComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
            ) {
                Text("Annulla Modifica")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rimuovi o Modifica un Prodotto", style = MaterialTheme.typography.titleLarge)
            GenericExposedDropdownMenu(
                items = products,
                selectedItem = selectedProduct,
                onItemSelected = { selectedProduct = it },
                itemText = { "${it.title} \n ${it.content}" },
                label = "Seleziona Prodotto",
                modifier = Modifier.widthIn(max = 400.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
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
                ) {
                    Text("Rimuovi Prodotto")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    onClick = {
                        onProductEdit(selectedProduct)
                    },
                    enabled = selectedProduct != null,
                ) {
                    Text("Modifica Prodotto")
                }
            }
        }
    }
}