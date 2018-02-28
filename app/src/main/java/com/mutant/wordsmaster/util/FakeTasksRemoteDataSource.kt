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

package com.mutant.wordsmaster.util

import android.support.annotation.VisibleForTesting
import com.google.common.collect.Lists
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsDataSource
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeTasksRemoteDataSource : WordsDataSource {

    override fun getWords(callback: WordsDataSource.LoadWordsCallback) {
        callback.onWordsLoaded(Lists.newArrayList<Word>(WORDS_SERVICE_DATA.values))
    }

    override fun getWord(wordId: String, callback: WordsDataSource.GetWordCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveWord(word: Word) {
        WORDS_SERVICE_DATA[word.id] = word
    }

    override fun refreshWords() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteWord(wordId: String) {
        WORDS_SERVICE_DATA.remove(wordId)
    }

    override fun deleteAllWords() {
        WORDS_SERVICE_DATA.clear()
    }

    override fun swapPosition(wordId1: String, wordId2: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @VisibleForTesting
    fun addWords(vararg words: Word) {
        for (word in words) {
            WORDS_SERVICE_DATA[word.id] = word
        }
    }

    companion object {

        private var INSTANCE: FakeTasksRemoteDataSource? = null

        private val WORDS_SERVICE_DATA = LinkedHashMap<String, Word>()

        val instance: FakeTasksRemoteDataSource?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = FakeTasksRemoteDataSource()
                }
                return INSTANCE
            }
    }
}
