package com.clebi.trainer.trainings

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

/**
 * TrainingStorage is responsible for writing and reading trainings using shared preferences.
 */
class SharedPrefsTrainingStorage(context: Context) : TrainingsStorage {

    companion object {
        private const val TAG = "SharedPrefsTrainingStorage"

        private const val PREFS_KEY = "trainings"

        /** storage key for trainings */
        private const val TRAININGS_KEY = "trainings_list_v1"

        private const val STORAGE_TYPE = "shared_preferences"
    }

    private val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    /** @see TrainingsStorage.storageType */
    override val storageType: String = FileTrainingStorage.STORAGE_TYPE

    /**
     * @see TrainingsStorage.isAccessible
     */
    override fun isAccessible(): Boolean {
        return true
    }

    /**
     * @see TrainingsStorage.read
     */
    override fun read(): TrainingsStorage.TrainingStorageContainer {
        val gson = Gson()
        val json = prefs.getString(TRAININGS_KEY, "[]")
        Log.d(TAG, "read json: $json")
        return try {
            gson.fromJson(json, TrainingsStorage.TrainingStorageContainer::class.java)
        } catch (exc: JsonSyntaxException) {
            TrainingsStorage.TrainingStorageContainer(0, STORAGE_TYPE, listOf())
        }
    }

    /**
     * @see TrainingsStorage.write
     */
    override fun write(saveTime: Long, trainings: List<Training>) {
        val gson = Gson()
        val json = gson.toJson(TrainingsStorage.TrainingStorageContainer(saveTime, STORAGE_TYPE, trainings))
        Log.d(TAG, "write json: $json")
        val editor = prefs.edit()
        editor.putString(TRAININGS_KEY, json)
        editor.apply()
    }
}
