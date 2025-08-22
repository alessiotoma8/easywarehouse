package easy.warehouse

data class Account(
    val type: TYPE,
    val name: String,
    val surname: String,
) {
    enum class TYPE {
        ADMIN, USER
    }
}