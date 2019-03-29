package com.mutant.wordsmaster.data.source.local

import androidx.annotation.VisibleForTesting
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.AppExecutors
import com.mutant.wordsmaster.util.trace.DebugHelper

/**
 * Concrete implementation of a data source as a db.
 */
class WordsLocalModel// Prevent direct instantiation.
private constructor(private val mAppExecutors: AppExecutors,
                    private val mWordsDao: WordsDao) : WordsLocalContract {

    /**
     * Note: [WordsLocalContract.LoadWordsCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getWords(callback: WordsLocalContract.LoadWordsCallback) {
        val runnable = Runnable {
            val words = mWordsDao.words
            mAppExecutors.mainThread().execute {
                if (words.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onWordsLoaded(words)
                }
            }
        }

        mAppExecutors.diskIO().execute(runnable)
    }

    override fun getWordByTitle(wordTitle: String, callback: WordsLocalContract.GetWordCallback) {
        val runnable = Runnable {
            val word = mWordsDao.getWordByTitle(wordTitle)
            mAppExecutors.mainThread().execute {
                if (word == null || word.isEmpty) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onWordLoaded(word, false)
                }
            }
        }

        mAppExecutors.diskIO().execute(runnable)
    }

    override fun saveWord(word: Word) {
        val saveRunnable = Runnable { mWordsDao.insertWord(word) }
        mAppExecutors.diskIO().execute(saveRunnable)
    }

    override fun refreshWords() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllWords() {
        val deleteRunnable = Runnable { mWordsDao.deleteWords() }
        mAppExecutors.diskIO().execute(deleteRunnable)
    }

    override fun deleteWord(wordId: String) {
        val deleteRunnable = Runnable { mWordsDao.deleteWordById(wordId) }
        mAppExecutors.diskIO().execute(deleteRunnable)
    }

    override fun swap(word1: Word, word2: Word) {
        val updateWord1 = word1.copy(id = word2.id)
        val updateWord2 = word2.copy(id = word1.id)
        val updateRunnable = Runnable {
            DebugHelper.d("mutant0113", "swap, ${updateWord1.title}, ${updateWord2.title}")
            mWordsDao.updateWords(updateWord1, updateWord2)
        }
        mAppExecutors.diskIO().execute(updateRunnable)
    }

    companion object {

        @Volatile
        private var INSTANCE: WordsLocalContract? = null

        fun getInstance(appExecutors: AppExecutors,
                        wordsDao: WordsDao): WordsLocalContract? {
            if (INSTANCE == null) {
                synchronized(WordsLocalContract::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = WordsLocalModel(appExecutors, wordsDao)
                    }
                }
            }
            return INSTANCE
        }

        @VisibleForTesting
        internal fun clearInstance() {
            INSTANCE = null
        }
    }
}