package easy.warehouse.product

fun ProductEntity.increaseCount() = copy(count = count + 1)

fun ProductEntity.decreaseCount() = copy(count = if (count > 0) count - 1 else 0)