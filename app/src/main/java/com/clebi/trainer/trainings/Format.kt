package com.clebi.trainer.trainings

import androidx.core.text.isDigitsOnly

/**
 * Provides utils function for formatting.
 */
object Format {
    /**
     * Format a duration.
     * @param duration in seconds.
     * @return formatted duration.
     */
    fun formatDuration(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration - hours * 3600) / 60
        val seconds = (duration - hours * 3600 - minutes * 60) % 60
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    /**
     * Get duration from a string.
     * @param duration the duration string
     * @return duration in seconds
     */
    fun durationFromString(duration: String): Int {
        val parts = duration.split(':')
        if (parts.count() < 3) {
            throw IllegalArgumentException("duration must have 3 components")
        }
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()
        return hours * 3600 + minutes * 60 + seconds
    }

    /**
     * Format a duration with short format.
     * @param duration in seconds.
     * @return formatted duration.
     */
    fun formatShortDuration(duration: Int): String {
        val minutes = duration / 60
        val seconds = (duration - minutes * 60) % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    /**
     * Get duration from a short formatted string.
     * @param duration the duration string
     * @return duration in seconds
     */
    fun shortDurationFromString(duration: String): Int {
        val parts = duration.split(':')
        if (parts.count() < 2) {
            throw IllegalArgumentException("duration must have 2 components")
        }
        if (!parts[0].isDigitsOnly()) {
            throw IllegalArgumentException("wrong format for minutes")
        }
        if (parts[1].length > 2 || !parts[1].isDigitsOnly()) {
            throw IllegalArgumentException("wrong format for seconds")
        }
        val minutes = parts[0].toInt()
        if (minutes < 0) {
            throw IllegalArgumentException("minutes value is mess than 0")
        }
        val seconds = parts[1].toInt()
        if (seconds < 0 || seconds > 60) {
            throw IllegalArgumentException("seconds value is less than 0 or greater than 60")
        }
        return minutes * 60 + seconds
    }
}
