/*
 * Copyright 2017, The Android Open Source Project
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

import android.arch.persistence.room.*
import com.mutant.wordsmaster.data.Word

/**
 * Data Access Object for the words table.
 */
@Dao
interface WordsDao {

    /**
     * Select all words from the words table.
     *
     * @return all words.
     */
    @get:Query("SELECT * FROM Words")
    val words: List<Word>

    /**
     * Select a word by id.
     *
     * @param wordId the word id.
     * @return the word with taskId.
     */
    @Query("SELECT * FROM words WHERE entryid = :wordId")
    fun getWordById(wordId: String): Word

    /**
     * Insert a word in the database. If the word already exists, replace it.
     *
     * @param word the word to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word)

    /**
     * Update a word.
     *
     * @param word word to be updated
     * @return the number of words updated. This should always be 1.
     */
    @Update
    fun updateWord(word: Word): Int

    /**
     * Delete a word by id.
     *
     * @return the number of words deleted. This should always be 1.
     */
    @Query("DELETE FROM words WHERE entryid = :wordId")
    fun deleteWordById(wordId: String): Int

    /**
     * Delete all words.
     */
    @Query("DELETE FROM words")
    fun deleteWords()

}
