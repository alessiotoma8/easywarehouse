package easy.warehouse.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Sostituto semplice di YearMonth (non incluso in kotlinx-datetime).
 */
data class YearMonth(val year: Int, val monthNumber: Int) {
    val month: Month = Month(monthNumber)

    fun lengthOfMonth(): Int {
        val firstDay = LocalDate(year, monthNumber, 1)
        val nextMonth = if (monthNumber == 12) LocalDate(year + 1, 1, 1)
        else LocalDate(year, monthNumber + 1, 1)
        return nextMonth.minus(1, DateTimeUnit.DAY).dayOfMonth
    }

    fun atDay(day: Int): LocalDate = LocalDate(year, monthNumber, day)

    fun plusMonths(n: Long): YearMonth {
        val total = (year * 12L + (monthNumber - 1)) + n
        val newYear = (total / 12).toInt()
        val newMonth = (total % 12 + 1).toInt()
        return YearMonth(newYear, newMonth)
    }

    fun minusMonths(n: Long): YearMonth = plusMonths(-n)

    companion object {
        fun from(date: LocalDate): YearMonth = YearMonth(date.year, date.monthNumber)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun JvmDatePicker(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit, // ora può essere null
    modifier: Modifier = Modifier,
    label: String = "Date",
) {
    var open by remember { mutableStateOf(false) }

    var month by remember(selectedDate) {
        mutableStateOf(
            selectedDate?.let { YearMonth.from(it) }
                ?: YearMonth.from(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                )
        )
    }

    fun LocalDate.format(pattern: String = "dd/MM/yyyy"): String {
        return pattern
            .replace("dd", dayOfMonth.toString().padStart(2, '0'))
            .replace("MM", monthNumber.toString().padStart(2, '0'))
            .replace("yyyy", year.toString())
    }

    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }


    Column(modifier) {
        OutlinedTextField(
            value = selectedDate?.format() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .onGloballyPositioned { coords ->
                    textFieldSize = coords.size
                },
            trailingIcon = {
                Row {
                    if (selectedDate != null) {
                        Text("❌", modifier = Modifier.clickable {
                            onDateSelected(null)
                        })
                        Spacer(Modifier.width(4.dp))
                    }
                }
            },
            leadingIcon = {
                Text("📅", modifier = Modifier.clickable { open = true })
            }
        )

        if (open) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { open = false },
                offset = IntOffset(
                    x = 0, y = textFieldSize.height
                ),
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth(0.5f).padding(16.dp)
                ) {
                    Column(Modifier.padding(12.dp).widthIn(min = 280.dp)) {
                        // Header month + year
                        Text(
                            label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            )
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                month = month.copy(year = month.year - 1)
                            }) { Text("«") }
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = month.year.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = {
                                month = month.copy(year = month.year + 1)
                            }) { Text("»") }
                        }

                        Spacer(Modifier.height(4.dp))

// Header mese
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { month = month.minusMonths(1) }) { Text("◀") }
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = month.month.name.lowercase()
                                    .replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { month = month.plusMonths(1) }) { Text("▶") }
                        }

                        Spacer(Modifier.height(8.dp))

                        CalendarGrid(
                            month = month,
                            selected = selectedDate,
                            onPick = {
                                onDateSelected(it)
                                open = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CalendarGrid(
    month: YearMonth,
    selected: LocalDate?,
    onPick: (LocalDate) -> Unit,
) {
    val days = remember(month) { buildMonthCells(month) }
    val weekLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Header giorni settimana
    Row(Modifier.fillMaxWidth()) {
        weekLabels.forEach { w ->
            Text(
                w,
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }

    Column(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        days.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    val isInMonth = date?.monthNumber == month.monthNumber
                    val isSelected = date != null && date == selected
                    val cellModifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(
                            if (isSelected) Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                            else Modifier
                        )

                    Box(cellModifier, contentAlignment = Alignment.Center) {
                        if (date != null) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isInMonth) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onPick(date) }
                                    .padding(top = 6.dp),
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            Spacer(Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

private fun buildMonthCells(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    // Monday=0 .. Sunday=6
    val lead = (first.dayOfWeek.isoDayNumber + 6) % 7
    val daysInMonth = month.lengthOfMonth()

    val cells = mutableListOf<LocalDate?>()
    repeat(lead) { cells += null }
    for (d in 1..daysInMonth) cells += month.atDay(d)
    while (cells.size % 7 != 0) cells += null
    while (cells.size < 42) cells += null // 6 settimane fisse
    return cells
}