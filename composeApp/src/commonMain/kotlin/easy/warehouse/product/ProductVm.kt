package easy.warehouse.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class PendingChange(
    val productId: Long,
    val title: String,
    val initialCount: Int, // Conteggio iniziale dal database
    val pendingCount: Int, // Conteggio attuale con le modifiche
    val delta: Int, // Differenza: pendingCount - initialCount
)

class ProductVm : ViewModel() {
    private val dbRepo = getRoomDatabase().getProductDao()

    private val products = dbRepo.getAllAsFlow()

    private val _pendingChanges = MutableStateFlow<Map<Long, PendingChange>>(emptyMap())
    val pendingChanges: StateFlow<Map<Long, PendingChange>> = _pendingChanges.asStateFlow()


    val displayProducts: StateFlow<List<ProductEntity>> =
        combine(products, pendingChanges) { dbProducts, changes ->
            dbProducts.map { product ->
                val pendingChange = changes[product.id]
                if (pendingChange != null) {
                    // Se ci sono modifiche, crea un nuovo ProductEntity con il conteggio aggiornato
                    product.copy(count = pendingChange.pendingCount)
                } else {
                    // Altrimenti, usa il prodotto dal database
                    product
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveChanges() = viewModelScope.launch {
        _pendingChanges.value.values.forEach { change ->
            val updatedProduct = dbRepo.getById(change.productId)?.copy(count = change.pendingCount)
            if (updatedProduct != null) {
                dbRepo.deleteById(updatedProduct.id)
                dbRepo.insert(updatedProduct)
            }
        }
        _pendingChanges.value = emptyMap()
    }

    fun clearChanges() {
        _pendingChanges.value = emptyMap()
    }

    private fun updatePendingCount(productId: Long, delta: Int) = viewModelScope.launch {
        val currentChanges = _pendingChanges.value.toMutableMap()
        val product = dbRepo.getById(productId) ?: return@launch
        val pendingChange = currentChanges[productId]
        val newCount = (pendingChange?.pendingCount ?: product.count) + delta

        val newPendingChange = PendingChange(
            productId = productId,
            title = product.title,
            initialCount = product.count,
            pendingCount = newCount,
            delta = newCount - product.count
        )
        currentChanges[productId] = newPendingChange

        if (newPendingChange.delta == 0) {
            currentChanges.remove(productId)
        }
        _pendingChanges.value = currentChanges
    }

    fun increaseCount(productId: Long) {
        updatePendingCount(productId, 1)
    }

    // Diminuisce il conteggio in sospeso
    fun decreaseCount(productId: Long) = viewModelScope.launch {
        val currentCount = _pendingChanges.value[productId]?.pendingCount ?: dbRepo.getById(productId)?.count
        if (currentCount != null && currentCount > 0) {
            updatePendingCount(productId, -1)
        }
    }

    fun insertProduct(product: ProductEntity) = viewModelScope.launch {
        dbRepo.insert(product)
    }

}