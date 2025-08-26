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

object AccountManager {
    private val username = "robby"
    private val pwd = "123"

    var isAdmin = false
        private set

    fun login(username: String, pwd: String): Boolean {
        isAdmin = username == this.username && pwd == this.pwd
        return isAdmin
    }
}