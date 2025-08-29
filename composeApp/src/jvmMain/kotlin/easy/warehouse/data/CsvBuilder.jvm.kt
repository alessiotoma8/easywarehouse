package easy.warehouse.data

import java.awt.Desktop
import java.io.File
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
/**
 * Apre la cartella "DbReports" utilizzando l'applicazione nativa del file explorer.
 * Questa operazione è specifica per i sistemi operativi desktop (Windows, macOS, Linux).
 */
actual fun openDbReportsFolder() {
    val userHome = System.getProperty("user.home")
    val downloadsDir = File(userHome, "DbReports")

    // Controlla se l'API Desktop è supportata e se la cartella esiste.
    if (Desktop.isDesktopSupported() && downloadsDir.exists()) {
        try {
            // Apre la cartella.
            Desktop.getDesktop().open(downloadsDir)
        } catch (e: Exception) {
            println("Impossibile aprire la cartella: ${e.message}")
            e.printStackTrace()
        }
    } else {
        println("L'API Desktop non è supportata o la cartella non esiste.")
    }
}