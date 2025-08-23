package easy.warehouse.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(getAppDataDir("EasyWarehouse4"), "easy_warehouse.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath
    )
}//"java.io.tmpdir"

fun getAppDataDir(appName: String): File {
    val userHome = System.getProperty("user.home")
    val osName = System.getProperty("os.name").lowercase()

    val basePath: String = when {
        osName.contains("mac") -> {
            "$userHome/Library/Application Support"
        }

        osName.contains("win") -> {
            System.getenv("APPDATA") ?: "$userHome/AppData/Roaming"
        }

        else -> { // Linux e altri UNIX-like
            System.getenv("XDG_DATA_HOME") ?: "$userHome/.local/share"
        }
    }

    val appDir = File(basePath, appName)

    if (!appDir.exists()) {
        appDir.mkdirs()
    }

    return appDir
}