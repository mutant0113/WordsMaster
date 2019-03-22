package com.mutant.wordsmaster.data.source.local

import androidx.room.*
import com.mutant.wordsmaster.data.source.model.Word

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
    @get:Query("SELECT * FROM Words ORDER BY ROWID DESC")
    val words: MutableList<Word>

    /**
     * Select a word by title.
     *
     * @param wordTitle the word title.
     * @return the word with wordId.
     */
    @Query("SELECT * FROM words WHERE title = :wordTitle")
    fun getWordByTitle(wordTitle: String?): Word?

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
     * @param words word to be updated
     * @return the number of words updated. This should always be 1.
     */
    @Update
    fun updateWords(vararg words: Word): Int

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