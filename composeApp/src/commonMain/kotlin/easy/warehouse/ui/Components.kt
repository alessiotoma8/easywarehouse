package easy.warehouse.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import easy.warehouse.destination.VehicleDestinationEntity
import easy.warehouse.employee.EmployeeEntity
import easy.warehouse.product.PendingChange
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import easy.warehouse.product.Utility


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WAppBar(
    text: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable() () -> Unit = {},
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                onBack?.let {
                    IconButton(onBack) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
                Text(
                    text,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        actions = {
            actions()
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
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
                    product.content?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                    onClick = { productVm.increaseCount(product.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Lascia")
                }
                Button(
                    onClick = { productVm.decreaseCount(product.id) },
                    enabled = product.count > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Prendi")
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
                val color = if (change.delta > 0)
                    MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
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
    onEmployeeSelected: (EmployeeEntity?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text("Utente", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        GenericExposedDropdownMenu(
            items = employees,
            selectedItem = selectedEmployee,
            onItemSelected = onEmployeeSelected,
            itemText = { "${it.name} ${it.surname}" },
            label = "Seleziona Utente",
            isError = selectedEmployee == null
        )
    }
}

@Composable
fun DestinationSelection(
    vehicles: List<VehicleDestinationEntity>,
    selectedVehicle: VehicleDestinationEntity?,
    onVehicleSelected: (VehicleDestinationEntity?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            "Destinazione",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        GenericExposedDropdownMenu(
            items = vehicles,
            selectedItem = selectedVehicle,
            onItemSelected = onVehicleSelected,
            itemText = { it.vehicleName + " - (${it.vehiclePlate}) " },
            label = "Seleziona Veicolo"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericExposedDropdownMenu(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    itemText: (T) -> String,
    label: String = "Seleziona Elemento",
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items
        else items.filter { itemText(it).contains(searchQuery, ignoreCase = true) }
    }
    val searchFocusRequester = remember { FocusRequester() }

    Row(verticalAlignment = Alignment.CenterVertically) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall),
        ) {
            TextField(
                readOnly = true,
                value = selectedItem?.let(itemText) ?: "",
                onValueChange = {},
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor(),
                isError = isError
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
                    if (filteredItems.indexOf(item) != filteredItems.count() - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
        if (selectedItem != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onItemSelected(null)
                    expanded = false
                },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Cancella selezione",
                    tint =  MaterialTheme.colorScheme.onError
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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


@Composable
fun ScreenContent(paddingValues: PaddingValues, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center // centra il contenuto nel Box
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}