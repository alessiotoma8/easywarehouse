package easy.warehouse.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easy.warehouse.db.getRoomDatabase
import kotlinx.coroutines.launch

class ProductVm : ViewModel() {
    private val dbRepo = getRoomDatabase().getDao()

    val products = dbRepo.getAllAsFlow()

    fun insertProduct(product: ProductEntity) = viewModelScope.launch {
        dbRepo.insert(product)
    }

    fun increaseCount(productId: Long) = viewModelScope.launch {
        val product = dbRepo.getById(id = productId)
        if (product != null) {
            val updatedProduct = product.increaseCount()
            dbRepo.deleteById(productId)
            dbRepo.insert(updatedProduct)
        }
    }

    fun decreaseCount(productId: Long) = viewModelScope.launch {
        val product = dbRepo.getById(id = productId)
        if (product != null) {
            val updatedProduct = product.decreaseCount()
            dbRepo.deleteById(productId)
            dbRepo.insert(updatedProduct)
        }
    }

}