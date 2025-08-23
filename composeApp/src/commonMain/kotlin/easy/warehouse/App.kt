package easy.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import easy.warehouse.product.PendingChange
import easy.warehouse.product.ProductEntity
import easy.warehouse.product.ProductVm
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WarehouseScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseScreen() {
    val productVm = viewModel<ProductVm>()
    val products by productVm.displayProducts.collectAsStateWithLifecycle(emptyList())
    val pendingChanges by productVm.pendingChanges.collectAsStateWithLifecycle(emptyMap())

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
            // Contenuto principale (pulsante "Add Product" e lista)
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        productVm.insertProduct(
                            ProductEntity(
                                title = "Product ${products.size + 1}",
                                content = "This is product number ${products.size + 1}",
                                count = 0
                            )
                        )
                    }) {
                        Text("Add Product")
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 400.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f), // Aggiungi un peso per far sì che la lista occupi lo spazio rimanente
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
                        isSaveEnabled = pendingChanges.isNotEmpty(),
                        isClearEnabled = pendingChanges.isNotEmpty(),
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
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

            // Pulsanti in basso a destra
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