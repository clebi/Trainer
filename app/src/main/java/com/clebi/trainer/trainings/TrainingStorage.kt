package com.clebi.trainer.trainings

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TrainingStorage is responsible for writing and reading trainings using shared preferences.
 */
object TrainingStorage {

    private const val TAG = "TrainingStorage"

    /** storage key for trainings */
    private const val TRAININGS_KEY = "trainings_list_v1"

    /**
     * Read the trainings.
     * @param prefs shared preferences reference.
     */
    fun read(prefs: SharedPreferences): List<Training> {
        val gson = Gson()
        val json = prefs.getString(TRAININGS_KEY, "[]")
        Log.d(TAG, "read json: $json")
        val trainingsType = object : TypeToken<List<Training>>() {}.type
        return gson.fromJson(json, trainingsType)
    }

    /**
     * Write the trainings.
     * @param trainings trainings to write.
     * @param editor the shared preferences editor.
     */
    fun write(trainings: List<Training>, prefs: SharedPreferences) {
        val gson = Gson()
        val json = gson.toJson(trainings)
        Log.d(TAG, "write json: $json")
        val editor = prefs.edit()
        editor.putString(TRAININGS_KEY, json)
        editor.apply()
    }
}
