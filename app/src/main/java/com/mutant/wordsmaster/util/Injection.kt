package com.mutant.wordsmaster.util

import android.content.Context

import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.local.WordsDatabase
import com.mutant.wordsmaster.data.source.local.WordsLocalModel
import com.mutant.wordsmaster.data.source.remote.WordsRemoteModel

/**
 * Enables injection of mock implementations for
 * [com.mutant.wordsmaster.data.source.WordsLocalContract] at compile time.
 * This is useful for testing, since it allows us to use a fake instance of the class to isolate
 * the dependencies and run a test hermetically.
 */
object Injection {

    fun provideTasksRepository(context: Context): WordsRepository {
        val database = WordsDatabase.getInstance(context)
        val appExecutors = AppExecutors()
        return WordsRepository.getInstance(WordsRemoteModel.getInstance(appExecutors)!!,
                WordsLocalModel.getInstance(appExecutors,
                        database!!.wordDao())!!)
    }
}
