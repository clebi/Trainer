package com.clebi.trainer.trainings

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TrainingStorage is responsible for writing and reading trainings using shared preferences.
 */
class SharedPrefsTrainingStorage(context: Context) : TrainingsStorage {

    companion object {
        private const val TAG = "TrainingStorage"

        private const val PREFS_KEY = "trainings"

        /** storage key for trainings */
        private const val TRAININGS_KEY = "trainings_list_v1"
    }

    private val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    /**
     * Read the trainings.
     * @return the list of training
     */
    override fun read(): List<Training> {
        val gson = Gson()
        val json = prefs.getString(TRAININGS_KEY, "[]")
        Log.d(TAG, "read json: $json")
        val trainingsType = object : TypeToken<List<Training>>() {}.type
        return gson.fromJson(json, trainingsType)
    }

    /**
     * Write the trainings.
     * @param trainings trainings to write.
     */
    override fun write(trainings: List<Training>) {
        val gson = Gson()
        val json = gson.toJson(trainings)
        Log.d(TAG, "write json: $json")
        val editor = prefs.edit()
        editor.putString(TRAININGS_KEY, json)
        editor.apply()
    }
}
