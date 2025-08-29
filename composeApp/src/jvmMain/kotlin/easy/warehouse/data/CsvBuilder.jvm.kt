package easy.warehouse.data

import java.nio.file.Files
import java.nio.file.Paths


actual fun saveToUserHomeFolder(fileName: String, content: ByteArray) {
    val userHome = System.getProperty("user.home")
    val downloadsDir = Paths.get(userHome, "DbReports")
    if (!Files.exists(downloadsDir)) {
        Files.createDirectories(downloadsDir)
    }
    val filePath = downloadsDir.resolve(fileName)
    Files.write(filePath, content)
    println("File salvato in: $filePath")
}
