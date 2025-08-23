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

class AccountManager{
    val username = "robby"
    val pwd = "123"
     fun login(username:String,pwd:String): Boolean{
        return username == this.username && pwd == this.pwd
    }
}