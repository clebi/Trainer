package com.clebi.trainer.trainings

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File

/**
 * FileTrainingStorage stores the trainings in file accessible on external.
 */
class FileTrainingStorage(private val context: Context) : TrainingsStorage {
    companion object {
        const val TAG = "FileTrainingStorage"
        const val filename = "trainings.json"
        const val STORAGE_TYPE = "external_file"
    }

    /** @see TrainingsStorage.storageType */
    override val storageType: String = STORAGE_TYPE

    /**
     * @see TrainingsStorage.isAccessible
     */
    override fun isAccessible(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * @see TrainingsStorage.read
     */
    override fun read(): TrainingsStorage.TrainingStorageContainer {
        val gson = Gson()
        val file = File(context.getExternalFilesDir(null), filename)
        if (!file.exists()) {
            return TrainingsStorage.TrainingStorageContainer(0, STORAGE_TYPE, listOf())
        }
        try {
            return gson.fromJson(file.bufferedReader(), TrainingsStorage.TrainingStorageContainer::class.java)
        } catch (exc: JsonSyntaxException) {
            return TrainingsStorage.TrainingStorageContainer(0, STORAGE_TYPE, listOf())
        }
    }

    /**
     * @see TrainingsStorage.write
     */
    override fun write(saveTime: Long, trainings: List<Training>) {
        val gson = Gson()
        val file = File(context.getExternalFilesDir(null), filename)
        Log.d(TAG, "trainings: $trainings")
        val json = gson.toJson(TrainingsStorage.TrainingStorageContainer(saveTime, STORAGE_TYPE, trainings))
        file.writeBytes(json.toByteArray())
    }
}
