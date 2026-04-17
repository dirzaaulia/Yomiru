package com.dirzaaulia.yomiru.util

import java.util.Locale

/**
 * Capitalizes the first letter of each word in a string and converts other letters in each word to lowercase.
 * Words are assumed to be separated by spaces.
 *
 * Examples:
 * "hello world" -> "Hello World"
 * "MY NAME" -> "My Name"
 * "  leading spaces" -> "  Leading Spaces" (Preserves leading/trailing spaces around words if needed by split behavior)
 * "first" -> "First"
 */
fun String.capitalizeWords(): String {
    if (this.isBlank()) return this // Handle empty or blank strings

    return this.split(' ').joinToString(" ") { word ->
        if (word.isNotEmpty()) {
            word.substring(0, 1).uppercase(Locale.getDefault()) +
                    word.substring(1).lowercase(Locale.getDefault())
        } else {
            "" // Handle potential empty strings if multiple spaces were used, though split usually handles this
        }
    }
}

// Keeping your old function for now, in case it's used elsewhere.
// You might want to deprecate or remove it if capitalizeWords is the intended replacement.
@Deprecated("Use capitalizeWords for more comprehensive word capitalization.", ReplaceWith("this.capitalizeWords()"))
fun String.capitalizeFirstWord(): String {
    if (isEmpty()) return this // Handle empty strings
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}
