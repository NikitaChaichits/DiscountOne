@file:Suppress("UnnecessaryVariable")

package com.digeltech.discountone.util.time

import android.content.Context
import com.digeltech.discountone.common.constants.date
import com.digeltech.discountone.common.constants.dayTime
import com.digeltech.discountone.common.constants.timeWithDate
import com.digeltech.discountone.util.locale.currentDeviceLocale
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/** used for non-locale specific formatters */
val fallbackLocale: Locale = Locale.US
val calendar: Calendar = Calendar.getInstance()

val timeWithDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timeWithDate, fallbackLocale)

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(date, fallbackLocale)

val dayTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dayTime, fallbackLocale)

private fun Long.toDateWithMonthWord(locale: Locale): String {

    calendar.timeInMillis = this

    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale)
    val date = "${calendar[Calendar.DAY_OF_MONTH]} $month, ${calendar[Calendar.YEAR]}"

    return date
}

fun getCurrentDateTime(context: Context): String {
    val formatter = SimpleDateFormat(timeWithDate, context.currentDeviceLocale())
    return formatter.format(timeNow)
}

fun getCurrentDate(): String {
    val formatter = SimpleDateFormat(date, fallbackLocale)
    return formatter.format(timeNow)
}

private fun Long.isYesterday(): Boolean {

    calendar.timeInMillis = this
    val notificationDay = calendar.get(Calendar.DAY_OF_YEAR)
    val notificationYear = calendar.get(Calendar.YEAR)

    calendar.timeInMillis = timeNow
    val dayNow = calendar.get(Calendar.DAY_OF_YEAR)
    val yearNow = calendar.get(Calendar.YEAR)

    val sameYear = yearNow == notificationYear
    val yesterday = dayNow - notificationDay == 1

    return sameYear && yesterday
}

/** Note: not locale specific. See [timeWithDateFormatter]. */
fun OffsetDateTime.toTimeWithDate(zone: ZoneId = ZoneId.systemDefault()): String =
    timeWithDateFormatter.withZone(zone).format(this)

/** Note: not locale specific. See [dayTimeFormatter]. */
fun OffsetDateTime.toDayTime(zone: ZoneId = ZoneId.systemDefault()): String =
    dayTimeFormatter.withZone(zone).format(this)

/** Note: not locale specific. See [dateFormatter]. */
fun LocalDate.toDate(): String = dateFormatter.format(this)

fun OffsetDateTime.toLocalisedDate(context: Context): String {
    val locale = context.currentDeviceLocale()
    val month = month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
    return "$dayOfMonth. $month $year"
}

fun LocalTime.toDayTime(): String = dayTimeFormatter.format(this)

fun formatDate(inputDateTime: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDateTime)
        return outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return inputDateTime
}