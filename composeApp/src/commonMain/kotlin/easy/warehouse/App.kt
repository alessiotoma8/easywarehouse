package easy.warehouse

import LoginScreen
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
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.admin.AdminScreen
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.destination.VehicleVm
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.employee.EmployeeVm
import easy.warehouse.product.PendingChange
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import org.jetbrains.compose.ui.tooling.preview.Preview

private val accountManager = AccountManager()

@Composable
@Preview
fun App() {
    MaterialTheme {
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
                if(!showLoginScreen){
                    isAdmin = false
                }
            }) {
                if(isAdmin) {
                    Text("Logout")
                }else {
                    Text("Login")
                }
            }
            if (!showLoginScreen) {
                WarehouseScreen()
            } else if(!isAdmin){
                LoginScreen { us, pwd ->
                    isAdmin = accountManager.login(us, pwd)
                }
            }else {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warehouse") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Utente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        DestinationSelection(
                            vehicles = vehicles,
                            selectedVehicle = selectedVehicle,
                            onVehicleSelected = { selectedVehicle = it }
                        )
                    }
                }

                Text(
                    text = "Prodotti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 400.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { product ->
                        ProductItem(product, productVm)
                    }
                }

                if (pendingChanges.isNotEmpty()) {
                    ChangesSummary(
                        pendingChanges = pendingChanges,
                        onSave = { productVm.saveChanges() },
                        onClear = { productVm.clearChanges() },
                        isSaveEnabled = selectedEmployee != null && selectedVehicle != null,
                        isClearEnabled = pendingChanges.isNotEmpty(),
                        modifier = Modifier.align(Alignment.End),
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity, productVm: ProductVm) {
    Row(
        modifier = Modifier
            .widthIn(min = 200.dp, max = 400.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(product.title, style = MaterialTheme.typography.titleMedium)
            Text(product.content, style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { productVm.increaseCount(product.id) },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Green,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            FilledIconButton(
                onClick = { productVm.decreaseCount(product.id) },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Remove")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Count")
                Text(
                    text = product.count.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
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
                "Changes to Apply",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            pendingChanges.values.forEach { change ->
                val action = if (change.delta > 0) "Added" else "Removed"
                val delta = if (change.delta > 0) change.delta else -change.delta
                val color = if (change.delta > 0) Color.Green else Color.Red

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
                    Text("Clear")
                }
                Button(
                    onClick = onSave,
                    enabled = isSaveEnabled
                ) {
                    Text("Save")
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
    val employeeVm = viewModel<EmployeeVm>()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        GenericExposedDropdownMenu(
            items = employees,
            selectedItem = selectedEmployee,
            onItemSelected = onEmployeeSelected,
            itemText = { "${it.name} ${it.surname}" },
            label = "Select User"
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
            label = "Select Vehicle"
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
    label: String = "Select Item",
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