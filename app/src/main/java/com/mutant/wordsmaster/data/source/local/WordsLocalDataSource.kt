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
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsDataSource
import com.mutant.wordsmaster.util.AppExecutors


/**
 * Concrete implementation of a data source as a db.
 */
class WordsLocalDataSource// Prevent direct instantiation.
private constructor(private val mAppExecutors: AppExecutors,
                    private val mWordsDao: WordsDao) : WordsDataSource {
    /**
     * Note: [WordsDataSource.LoadWordsCallback.onDataNotAvailable] is fired if the database doesn't exist
     * or the table is empty.
     */
    override fun getWords(callback: WordsDataSource.LoadWordsCallback) {
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

    override fun getWord(wordId: String, callback: WordsDataSource.GetWordCallback) {
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

    override fun swapPosition(wordId1: String, wordId2: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        @Volatile
        private var INSTANCE: WordsLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors,
                        wordsDao: WordsDao): WordsLocalDataSource? {
            if (INSTANCE == null) {
                synchronized(WordsLocalDataSource::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = WordsLocalDataSource(appExecutors, wordsDao)
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
