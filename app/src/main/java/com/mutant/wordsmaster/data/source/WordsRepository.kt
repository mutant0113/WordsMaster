package com.mutant.wordsmaster.data.source

import android.content.Context
import androidx.room.Entity
import com.mutant.wordsmaster.data.source.model.Word
import java.util.*

@Entity(tableName = "words")
class WordsRepository private constructor(private val mWordsRemoteModel: WordsRemoteContract,
                                          private val mWordsLocalModel: WordsLocalContract) : WordsRemoteContract, WordsLocalContract {

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    private var mCachedWords: MutableMap<String, Word> = linkedMapOf()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private var mCacheIsDirty = false

    override fun getWords(callback: WordsLocalContract.LoadWordsCallback) {
        if (mCachedWords.isNotEmpty() && !mCacheIsDirty) {
            callback.onWordsLoaded(LinkedList(mCachedWords.values))
        }

        mWordsLocalModel.getWords(object : WordsLocalContract.LoadWordsCallback {

            override fun onWordsLoaded(words: MutableList<Word>) {
                refreshCache(words)
                callback.onWordsLoaded(words)
            }

            override fun onDataNotAvailable() {
                // TODO use firebase in the future
//              getWordsFromRemoteDataSource(callback)
                callback.onDataNotAvailable()
            }

        })
    }

    override fun getWordByTitle(context: Context, wordTitle: String?, callback: WordsLocalContract.GetWordCallback) {
        if(wordTitle.isNullOrBlank()) callback.onDataNotAvailable()
        mWordsLocalModel.getWordByTitle(context, wordTitle, object : WordsLocalContract.GetWordCallback {

            override fun onWordLoaded(word: Word, isNewWord: Boolean) {
                callback.onWordLoaded(word, isNewWord)
            }

            override fun onDataNotAvailable() {
                mWordsRemoteModel.getWordByTitle(context, wordTitle, callback)
            }

        })
    }

    private fun refreshCache(words: List<Word>) {
        mCachedWords.clear()
        for (word in words) {
            mCachedWords[word.id] = word
        }
    }

    override fun refreshWords() {
        mCacheIsDirty = true
    }

    override fun saveWord(word: Word) {
        mWordsLocalModel.saveWord(word)
        mCachedWords[word.id] = word
    }

    override fun deleteWord(wordId: String) {
        mWordsLocalModel.deleteWord(wordId)
        mCachedWords.remove(wordId)
    }

    override fun deleteAllWords() {
        mWordsLocalModel.deleteAllWords()
        mCachedWords.clear()
    }

    override fun swap(word1: Word, word2: Word) {
        mWordsLocalModel.swap(word1, word2)
        val updateWord1 = word1.copy(id = word2.id)
        val updateWord2 = word2.copy(id = word1.id)
        mCachedWords[updateWord1.id] = updateWord1
        mCachedWords[updateWord2.id] = updateWord2
    }

    companion object {

        @Volatile private var INSTANCE: WordsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param wordsRemoteModel the backend data source
         * @param wordsLocalModel  the device storage data source
         * @return the [WordsRepository] instance
         */
        fun getInstance(wordsRemoteModel: WordsRemoteContract,
                        wordsLocalModel: WordsLocalContract): WordsRepository {
            if (INSTANCE == null) {
                INSTANCE = WordsRepository(wordsRemoteModel, wordsLocalModel)
            }
            return INSTANCE!!
        }

    }

}
