package com.dirzaaulia.yomiru.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

fun hasDatePassed(dateTimeString: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'")
    return try {
        // Parse the input string to a LocalDateTime
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)

        // Convert the LocalDateTime to a ZonedDateTime in UTC
        val zonedDateTimeUTC = dateTime.atZone(ZoneId.of("UTC"))

        // Convert the ZonedDateTime to the system's default time zone
        val zonedDateTimeLocal = zonedDateTimeUTC.withZoneSameInstant(ZoneId.systemDefault())

        // Get the current time in the system's default time zone
        val currentDateTimeLocal = ZonedDateTime.now(ZoneId.systemDefault())

        // Check if the event's time is before the current time
        zonedDateTimeLocal.isBefore(currentDateTimeLocal)
    } catch (e: Exception) {
        // Handle parsing errors (e.g., invalid date format)
        false
    }
}

/**
 * Extension function to format a date-time string (in the format "yyyy-MM-dd HH:mm:ss'Z'")
 * into the local device's time zone.
 *
 * @param outputPattern The desired output pattern for the formatted date-time string.
 *                      Defaults to "yyyy-MM-dd HH:mm:ss" if not specified.
 * @return The formatted date-time string in the local time zone, or null if the input
 *         string is invalid.
 */
fun String.formatToLocalTimezone(outputPattern: String = "yyyy-MM-dd HH:mm:ss"): String? {
    return try {
        // Define the input format with 'Z' at the end for UTC
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'")

        // Parse the input string to LocalDateTime (assuming it's in UTC due to 'Z')
        val utcDateTime = LocalDateTime.parse(this, inputFormatter)

        // Convert LocalDateTime to ZonedDateTime with UTC zone
        val utcZonedDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))

        // Convert to the local device's time zone
        val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

        // Format the ZonedDateTime to the desired output pattern
        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern)
        localZonedDateTime.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        // Handle parsing errors
        println("Error parsing date-time string: $this, error: ${e.message}")
        null
    } catch (e: Exception) {
        // Handle other potential exceptions
        println("An unexpected error occurred: ${e.message}")
        null
    }
}

/**
 * Formats two date-time strings into a range of dates in the format "dd-dd MMM"
 * and automatically converts them to the local device's time zone.
 *
 * @param dateTimeString1 The first date-time string in the format "yyyy-MM-dd HH:mm:ss'Z'".
 * @param dateTimeString2 The second date-time string in the format "yyyy-MM-dd HH:mm:ss'Z'".
 * @return A formatted string representing the date range in the local time zone (e.g., "25-28 Dec") or
 *         null if the input date-times are invalid or in the wrong format.
 */
fun formatDateTimeRangeToLocal(dateTimeString1: String, dateTimeString2: String): String? {
    return try {
        // Define the input formatter for the date-time strings (with 'Z' for UTC)
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'")

        // Parse the date-time strings into ZonedDateTime objects (assuming UTC)
        val zonedDateTime1 = LocalDateTime.parse(dateTimeString1, inputFormatter)
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.systemDefault())

        val zonedDateTime2 = LocalDateTime.parse(dateTimeString2, inputFormatter)
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.systemDefault())

        // Determine the start and end date-times
        val startDateTime =
            if (zonedDateTime1.isBefore(zonedDateTime2)) zonedDateTime1 else zonedDateTime2
        val endDateTime =
            if (zonedDateTime1.isAfter(zonedDateTime2)) zonedDateTime1 else zonedDateTime2

        // Format the dates into the desired output pattern
        val outputFormatter = DateTimeFormatter.ofPattern("dd")
        val outputMonthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)

        // Format start and end dates
        val formattedStartDate = startDateTime.format(outputFormatter)
        val formattedEndDate = endDateTime.format(outputFormatter)

        // Format the month
        val month = startDateTime.format(outputMonthFormatter)

        // Combine into date range string
        "$formattedStartDate-$formattedEndDate $month"
    } catch (e: DateTimeParseException) {
        // Handle parsing errors
        println("Error parsing date-time strings: $dateTimeString1, $dateTimeString2, error: ${e.message}")
        null
    } catch (e: Exception) {
        // Handle other potential exceptions
        println("An unexpected error occurred: ${e.message}")
        null
    }
}