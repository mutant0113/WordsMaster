package com.mutant.wordsmaster.data.source

import android.arch.persistence.room.Entity
import com.mutant.wordsmaster.data.source.model.Word

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
            callback.onWordsLoaded(ArrayList<Word>(mCachedWords.values))
        }

        mWordsLocalModel.getWords(object : WordsLocalContract.LoadWordsCallback {

            override fun onWordsLoaded(words: List<Word>) {
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

    override fun getWord(wordId: String, callback: WordsLocalContract.GetWordCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun getWordFromLocal(wordId: String, callback: WordsLocalContract.GetWordCallback) {
        mWordsLocalModel.getWord(wordId, object : WordsLocalContract.GetWordCallback {

            override fun onWordLoaded(word: Word) {
                callback.onWordLoaded(word)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    override fun saveWord(word: Word) {
        mWordsLocalModel.saveWord(word)
//        mWordsRemoteModel.saveWord(word)
        mCachedWords[word.id] = word
    }

    override fun deleteWord(wordId: String) {
        mWordsLocalModel.deleteWord(wordId)
//        mWordsRemoteModel.deleteWord(wordId)
        mCachedWords.remove(wordId)
    }

    override fun deleteAllWords() {
        mWordsLocalModel.deleteAllWords()
//        mWordsRemoteModel.deleteAllWords()
        mCachedWords.clear()
    }

    override fun swapPosition(wordId1: String, wordId2: String) {
        mWordsLocalModel.swapPosition(wordId1, wordId2)
//        mWordsRemoteModel.swapPosition(wordId1, wordId2)
//        mCachedWords.
    }

    override fun parseHtml(html: String, callback: WordsRemoteContract.GetWordCallback){
        return mWordsRemoteModel.parseHtml(html, callback)
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
