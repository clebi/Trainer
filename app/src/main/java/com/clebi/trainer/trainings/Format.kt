package com.clebi.trainer.trainings

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
}
