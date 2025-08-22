package easy.warehouse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform