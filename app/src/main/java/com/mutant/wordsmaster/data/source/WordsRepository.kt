package com.mutant.wordsmaster.data.source

import android.arch.persistence.room.Entity
import com.mutant.wordsmaster.data.Word

@Entity(tableName = "words")
class WordsRepository private constructor(private val mWordsRemoteDataSource: WordsDataSource,
                              private val mWordsLocalDataSource: WordsDataSource): WordsDataSource {
    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    private var mCachedWords: MutableMap<String, Word> = linkedMapOf()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    private var mCacheIsDirty = false

    override fun getWords(callback: WordsDataSource.LoadWordsCallback) {
        if(mCachedWords.isNotEmpty() && !mCacheIsDirty) {
            callback.onWordsLoaded(ArrayList<Word>(mCachedWords.values))
        }

        if(!mCacheIsDirty) {
            getWordsFromRemoteDataSource(callback)
        } else {
            mWordsLocalDataSource.getWords(object : WordsDataSource.LoadWordsCallback {

                override fun onWordsLoaded(words: List<Word>) {
                    refreshCache(words)
                    callback.onWordsLoaded(words)
                }

                override fun onDataNotAvailable() {
                    getWordsFromRemoteDataSource(callback)
                }

            })
        }
    }

    private fun getWordsFromRemoteDataSource(callback: WordsDataSource.LoadWordsCallback) {
        mWordsRemoteDataSource.getWords(object : WordsDataSource.LoadWordsCallback {

            override fun onWordsLoaded(words: List<Word>) {
                refreshCache(words)
                refreshLocalDataSource(words)
                callback.onWordsLoaded(ArrayList<Word>(mCachedWords.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshLocalDataSource(tasks: List<Word>) {
        mWordsLocalDataSource.deleteAllWords()
        for (task in tasks) {
            mWordsLocalDataSource.saveWord(task)
        }
    }

    private fun refreshCache(words: List<Word>) {
        mCachedWords.clear()
        for (word in words) {
            mCachedWords[word.id] = word
        }
        mCacheIsDirty = false
    }

    override fun getWord(wordId: String, callback: WordsDataSource.GetWordCallback) {

    }

    override fun saveWord(word: Word) {
        mWordsLocalDataSource.saveWord(word)
//        mWordsRemoteDataSource.saveWord(word)
        mCachedWords[word.id] = word
    }

    override fun deleteWord(wordId: String) {
        mWordsLocalDataSource.deleteWord(wordId)
//        mWordsRemoteDataSource.deleteWord(wordId)
        mCachedWords.remove(wordId)
    }

    override fun deleteAllWords() {
        mWordsLocalDataSource.deleteAllWords()
//        mWordsRemoteDataSource.deleteAllWords()
        mCachedWords.clear()
    }

    override fun refreshWords() {
        mCacheIsDirty = true
    }

    override fun swapPosition(wordId1: String, wordId2: String) {
        mWordsLocalDataSource.swapPosition(wordId1, wordId2)
//        mWordsRemoteDataSource.swapPosition(wordId1, wordId2)
//        mCachedWords.
    }

    companion object {

        private var INSTANCE: WordsRepository? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param wordsRemoteDataSource the backend data source
         * @param wordsLocalDataSource  the device storage data source
         * @return the [WordsRepository] instance
         */
        fun getInstance(wordsRemoteDataSource: WordsDataSource,
                        wordsLocalDataSource: WordsDataSource): WordsRepository? {
            if (INSTANCE == null) {
                INSTANCE = WordsRepository(wordsRemoteDataSource, wordsLocalDataSource)
            }
            return INSTANCE
        }
    }

}
