package easy.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val products by productVm.products.collectAsStateWithLifecycle(emptyList())

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warehouse") }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 400.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductItem(product, productVm)
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity, productVm: ProductVm) {
    Row(
        modifier = Modifier
            .widthIn(min = 400.dp, max = 600.dp)
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
                onClick = {
                    productVm.increaseCount(product.id)
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Green,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            FilledIconButton(
                onClick = {
                    productVm.decreaseCount(product.id)
                },
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

//fare report con diff prima e dopo dei count
