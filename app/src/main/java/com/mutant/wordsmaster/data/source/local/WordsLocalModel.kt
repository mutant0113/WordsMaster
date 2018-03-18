/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mutant.wordsmaster.data.source.local

import android.support.annotation.VisibleForTesting
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
            mAppExecutors.mainThread().execute({
                if (words.isEmpty()) {
                    // This will be called if the table is new or just empty.
                    callback.onDataNotAvailable()
                } else {
                    callback.onWordsLoaded(words)
                }
            })
        }

        mAppExecutors.diskIO().execute(runnable)
    }

    override fun getWord(wordId: String, callback: WordsLocalContract.GetWordCallback) {
        val runnable = Runnable {
            val word = mWordsDao.getWordById(wordId)
            mAppExecutors.mainThread().execute({
                if(word.isEmpty) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onWordLoaded(word)
                }
            })
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
