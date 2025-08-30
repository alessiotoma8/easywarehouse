package easy.warehouse.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import easy.warehouse.ui.SearchBar
import easy.warehouse.ui.WAppBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

// Data models
data class Field<T>(
    val label: String,
    val state: MutableState<T>,
    val isRequired: Boolean = false,
    val isEnabled: Boolean = true,
)

val Green2 = Color(0xFF4CAF50)
val Red2 = Color(0xFFF44336)
val Blue2 = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AdminScreen(onLogoutClick: () -> Unit = {}, onReportClick: () -> Unit = {}) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Utenti", "Veicoli", "Prodotti")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedEmployeeForEdit by remember { mutableStateOf<EmployeeEntity?>(null) }
    var selectedVehicleForEdit by remember { mutableStateOf<VehicleDestinationEntity?>(null) }
    var selectedProductForEdit by remember { mutableStateOf<ProductEntity?>(null) }

    val showSnackbar: (String) -> Unit = { message ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            WAppBar("Pannello di Amministrazione") {
                Button(
                    onClick = onReportClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                ) {
                    Text("Report")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                ) {
                    Text("Esci")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        ScreenContent(innerPadding) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TabRow(selectedTabIndex = selectedTabIndex, modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Left Panel - Aggiungi
                    Surface(
                        modifier = Modifier.weight(1f),
                        tonalElevation = 2.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Aggiungi",
                                    tint = Green2
                                )
                                Text(
                                    "Aggiungi un nuovo elemento",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            when (selectedTabIndex) {
                                0 -> UserAddSection(onEditComplete = showSnackbar)
                                1 -> VehicleAddSection(onEditComplete = showSnackbar)
                                2 -> ProductAddSection(onEditComplete = showSnackbar)
                            }
                        }
                    }

                    // Right Panel - Rimuovi / Modifica
                    Surface(
                        modifier = Modifier.weight(1f),
                        tonalElevation = 2.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Rimuovi/Modifica",
                                    tint = Blue2
                                )
                                Text(
                                    "Rimuovi o Modifica un elemento",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            when (selectedTabIndex) {
                                0 -> UserRemoveSection(
                                    selectedEmployeeForEdit,
                                    onEmployeeEdit = { selectedEmployeeForEdit = it },
                                    onEditComplete = {
                                        selectedEmployeeForEdit = null
                                        showSnackbar(it)
                                    }
                                )

                                1 -> VehicleRemoveSection(
                                    selectedVehicleForEdit,
                                    onVehicleEdit = { selectedVehicleForEdit = it },
                                    onEditComplete = {
                                        selectedVehicleForEdit = null
                                        showSnackbar(it)
                                    }
                                )

                                2 -> ProductRemoveSection(
                                    selectedProductForEdit,
                                    onProductEdit = { selectedProductForEdit = it },
                                    onEditComplete = {
                                        selectedProductForEdit = null
                                        showSnackbar(it)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// GENERIC COMPONENTS
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseAddTab(
    title: String,
    fields: List<Field<String>>,
    saveAction: () -> Unit,
    saveButtonText: String,
    onEditComplete: (String) -> Unit,
    snackbarMessage: String,
    additioncondition: Boolean = true,
    editContent: @Composable () -> Unit = {
        fields.forEach { field ->
            OutlinedTextField(
                value = field.state.value,
                onValueChange = { field.state.value = it },
                label = { Text(field.label) },
                modifier = Modifier.fillMaxWidth(0.8f),
                enabled = field.isEnabled
            )
        }
    },
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        editContent()
        Spacer(Modifier)
        Button(
            onClick = {
                val allFieldsFilled = fields.all { !it.isRequired || it.state.value.isNotBlank() }
                if (allFieldsFilled) {
                    saveAction()
                    onEditComplete(snackbarMessage)
                }
            },
            enabled = fields.all { !it.isRequired || it.state.value.isNotBlank() } && additioncondition
        ) {
            Text(saveButtonText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BaseRemoveTab(
    title: String,
    listFlow: Flow<List<T>>,
    selectedItemForEdit: T?,
    onEdit: (T?) -> Unit,
    onEditComplete: (String) -> Unit,
    addEditContent: @Composable (T?, (String) -> Unit) -> Unit,
    listContent: @Composable (List<T>, (T) -> Unit, (String) -> Unit) -> Unit,
) {
    val items by listFlow.collectAsState(initial = emptyList())

    if (selectedItemForEdit != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            addEditContent(selectedItemForEdit, onEditComplete)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = { onEditComplete("Annullata modifica") }
            ) {
                Text("Annulla Modifica")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            listContent(items, onEdit, onEditComplete)
        }
    }
}

// -------------------------------------------------------------------------------------------------
// USER SECTIONS
// -------------------------------------------------------------------------------------------------

@Composable
fun UserAddSection(
    employeeToEdit: EmployeeEntity? = null,
    onEditComplete: (String) -> Unit,
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
                employeeVm.updateEmployee(
                    name = nameState.value,
                    surname = surnameState.value,
                    id = employeeToEdit.id,
                )
            }
            nameState.value = ""
            surnameState.value = ""
        },
        saveButtonText = if (employeeToEdit == null) "Aggiungi Utente" else "Salva Modifiche",
        onEditComplete = onEditComplete,
        snackbarMessage = if (employeeToEdit == null) "Utente aggiunto con successo!" else "Utente modificato con successo!"
    )
}

@Composable
fun UserRemoveSection(
    selectedEmployeeForEdit: EmployeeEntity?,
    onEmployeeEdit: (EmployeeEntity?) -> Unit,
    onEditComplete: (String) -> Unit,
) {
    val employeeVm = viewModel<EmployeeVm>()
    var searchQuery by remember { mutableStateOf("") }


    BaseRemoveTab(
        title = "Seleziona un Utente",
        listFlow = employeeVm.employees,
        selectedItemForEdit = selectedEmployeeForEdit,
        onEdit = onEmployeeEdit,
        onEditComplete = onEditComplete,
        addEditContent = { employee, onComplete ->
            UserAddSection(employee, onComplete)
        },
        listContent = { items, onEdit, onComplete ->
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            val filtered = items.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.surname.contains(searchQuery, ignoreCase = true)
            }
            UserCardList(
                users = filtered,
                onEdit = onEdit,
                onDelete = {
                    employeeVm.removeEmployee(it.id)
                    onComplete("Utente rimosso con successo!")
                }
            )
        }
    )
}

@Composable
fun UserCardList(
    users: List<EmployeeEntity>,
    onEdit: (EmployeeEntity) -> Unit,
    onDelete: (EmployeeEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        users.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${user.name} ${user.surname}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onEdit(user) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica")
                        }
                        IconButton(onClick = { onDelete(user) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Elimina",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// VEHICLE SECTIONS
// -------------------------------------------------------------------------------------------------

@Composable
fun VehicleAddSection(
    vehicleToEdit: VehicleDestinationEntity? = null,
    onEditComplete: (String) -> Unit,
) {
    val vehicleVm = viewModel<VehicleVm>()
    val vehiclePlateState = remember { mutableStateOf(vehicleToEdit?.vehiclePlate ?: "") }
    val vehicleNameState = remember { mutableStateOf(vehicleToEdit?.vehicleName ?: "") }

    val fields = listOf(
        Field("Targa Veicolo", vehiclePlateState, isRequired = true),
        Field("Nome Veicolo", vehicleNameState, isRequired = true)
    )

    BaseAddTab(
        title = if (vehicleToEdit == null) "Inserisci un nuovo Veicolo" else "Modifica Veicolo",
        fields = fields,
        saveAction = {
            if (vehicleToEdit == null) {
                vehicleVm.addVehicle(plate = vehiclePlateState.value, name = vehicleNameState.value)
            } else {
                vehicleVm.updateVehicle(
                    plate = vehiclePlateState.value,
                    name = vehicleNameState.value,
                    id = vehicleToEdit.id
                )
            }
            vehiclePlateState.value = ""
            vehicleNameState.value = ""
        },
        saveButtonText = if (vehicleToEdit == null) "Aggiungi Veicolo" else "Salva Modifiche",
        onEditComplete = onEditComplete,
        snackbarMessage = if (vehicleToEdit == null) "Veicolo aggiunto con successo!" else "Veicolo modificato con successo!"
    )
}

@Composable
fun VehicleRemoveSection(
    selectedVehicleForEdit: VehicleDestinationEntity?,
    onVehicleEdit: (VehicleDestinationEntity?) -> Unit,
    onEditComplete: (String) -> Unit,
) {
    val vehicleVm = viewModel<VehicleVm>()
    var searchQuery by remember { mutableStateOf("") }

    BaseRemoveTab(
        title = "Seleziona un Veicolo",
        listFlow = vehicleVm.vehicles,
        selectedItemForEdit = selectedVehicleForEdit,
        onEdit = onVehicleEdit,
        onEditComplete = onEditComplete,
        addEditContent = { vehicle, onComplete ->
            VehicleAddSection(vehicle, onComplete)
        },
        listContent = { items, onEdit, onComplete ->
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            val filtered = items.filter {
                it.vehicleName.contains(searchQuery, ignoreCase = true) ||
                        it.vehiclePlate.contains(searchQuery, ignoreCase = true)
            }
            VehicleCardList(
                vehicles = filtered,
                onEdit = onEdit,
                onDelete = {
                    vehicleVm.removeVehicle(it.id)
                    onComplete("Veicolo rimosso con successo!")
                }
            )
        }
    )
}

@Composable
fun VehicleCardList(
    vehicles: List<VehicleDestinationEntity>,
    onEdit: (VehicleDestinationEntity) -> Unit,
    onDelete: (VehicleDestinationEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        vehicles.forEach { vehicle ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vehicle.vehicleName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Targa: ${vehicle.vehiclePlate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onEdit(vehicle) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica")
                        }
                        IconButton(onClick = { onDelete(vehicle) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Elimina",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// PRODUCT SECTIONS
// -------------------------------------------------------------------------------------------------

@Composable
fun ProductAddSection(
    productToEdit: ProductEntity? = null,
    onEditComplete: (String) -> Unit,
) {
    val productVm = viewModel<ProductVm>()
    val titleState = remember { mutableStateOf(productToEdit?.title ?: "") }
    val contentState = remember { mutableStateOf(productToEdit?.content ?: "") }
    val countState = remember { mutableStateOf(productToEdit?.count?.toString() ?: "") }
    var utility by remember { mutableStateOf(productToEdit?.utility) }

    BaseAddTab(
        title = if (productToEdit == null) "Inserisci un nuovo Prodotto" else "Modifica Prodotto",
        fields = listOf(
            Field("Titolo Prodotto", titleState, isRequired = true),
            Field("Descrizione", contentState),
            Field("Quantità", countState, isRequired = true)
        ),
        additioncondition = utility != null,
        saveAction = {
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
                    val productEntity = ProductEntity(
                        id = productToEdit.id,
                        title = titleState.value,
                        content = contentState.value,
                        count = countState.value.toIntOrNull() ?: 0,
                        utility = utility ?: Utility.ALTRI
                    )
                    productVm.updateProduct(productEntity)
                }
                titleState.value = ""
                contentState.value = ""
                countState.value = ""
                utility = null
            }
        },
        saveButtonText = if (productToEdit == null) "Aggiungi Prodotto" else "Salva Modifiche",
        onEditComplete = onEditComplete,
        snackbarMessage = if (productToEdit == null) "Prodotto aggiunto con successo!" else "Prodotto modificato con successo!",
        editContent = {
            OutlinedTextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                label = { Text("Titolo Prodotto") },
                modifier = Modifier.fillMaxWidth(0.8f),
            )
            OutlinedTextField(
                value = contentState.value,
                onValueChange = { contentState.value = it },
                label = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth(0.8f),
            )
            OutlinedTextField(
                value = countState.value,
                onValueChange = { countState.value = it },
                label = { Text("Quantità") },
                modifier = Modifier.fillMaxWidth(0.8f),
            )
            GenericExposedDropdownMenu(
                items = Utility.entries,
                selectedItem = utility,
                onItemSelected = { utility = it },
                itemText = { it.displayName },
                label = "Seleziona Settore",
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }
    )
}

@Composable
fun ProductRemoveSection(
    selectedProductForEdit: ProductEntity?,
    onProductEdit: (ProductEntity?) -> Unit,
    onEditComplete: (String) -> Unit,
) {
    val productVm = viewModel<ProductVm>()
    var searchQuery by remember { mutableStateOf("") }

    BaseRemoveTab(
        title = "Seleziona un Prodotto",
        listFlow = productVm.displayProducts,
        selectedItemForEdit = selectedProductForEdit,
        onEdit = onProductEdit,
        onEditComplete = onEditComplete,
        addEditContent = { product, onComplete ->
            ProductAddSection(product, onComplete)
        },
        listContent = { items, onEdit, onComplete ->
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            val filtered = items.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        (it.content?.contains(searchQuery, ignoreCase = true) ?: false)
            }
            ProductCardList(
                products = filtered,
                onEdit = onEdit,
                onDelete = {
                    productVm.removeProduct(it.id)
                    onComplete("Prodotto rimosso con successo!")
                }
            )
        }
    )
}

@Composable
fun ProductCardList(
    products: List<ProductEntity>,
    onEdit: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        products.forEach { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (product.content?.isNotBlank() ?: false) {
                            Text(
                                text = product.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Quantità: ${product.count}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Settore: ${product.utility.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { onEdit(product) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifica")
                        }
                        IconButton(onClick = { onDelete(product) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Elimina",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}