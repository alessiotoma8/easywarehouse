package easy.warehouse.data

import easy.warehouse.report.ReportEntity
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun exportReportsToCsv(reports: List<ReportEntity>): String {
    val csvBuilder = StringBuilder()
    // Define the delimiter you want to use
    val delimiter = ";"

    // intestazioni CSV
    val headers = listOf(
        "id","date","time","employeeId","employeeName","employeeSurname",
        "productId","productTitle","productDesc","productUtility",
        "productCountChange","vehiclePlate","vehicleName"
    )
    // Use the delimiter here
    csvBuilder.append(headers.joinToString(delimiter) { "\"$it\"" })
    csvBuilder.append("\n")

    // funzione per gestire virgolette e campi vuoti
    fun escapeCsvField(value: String?): String {
        val escaped = value?.replace("\"", "\"\"") ?: ""
        return "\"$escaped\""
    }

    // righe CSV
    reports.forEach { report ->
        val row = listOf(
            report.id.toString(),
            report.date,
            report.time,
            report.employeeId.toString(),
            report.employeeName,
            report.employeeSurname,
            report.productId.toString(),
            report.productTitle,
            report.productDesc,
            report.productUtility.displayName,
            report.productCountChange.toString(),
            report.vehiclePlate,
            report.vehicleName
        ).joinToString(delimiter) { escapeCsvField(it) } // And here

        csvBuilder.append(row).append("\n")
    }

    return csvBuilder.toString()
}

@OptIn(ExperimentalTime::class)
suspend fun exportDatabaseToCsv(reports: List<ReportEntity>) {
    val csv = exportReportsToCsv(reports)
    val bytes = csv.encodeToByteArray()
    val dateTimeId = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val formatted= "${dateTimeId.dayOfMonth}_${dateTimeId.monthNumber}_${dateTimeId.year}_${dateTimeId.hour}_${dateTimeId.minute}_${dateTimeId.second}"
    saveToUserHomeFolder(fileName = "reports_export_$formatted.csv", content = bytes)
}

// commonMain
expect fun saveToUserHomeFolder(fileName: String, content: ByteArray)
expect fun openDbReportsFolder()
