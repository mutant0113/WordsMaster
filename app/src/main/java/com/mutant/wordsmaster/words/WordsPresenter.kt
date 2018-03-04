package com.mutant.wordsmaster.words

import android.app.Activity
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRepository

class WordsPresenter(private val mWordsRepository: WordsRepository?,
                     private val mWordsView: WordsContract.View) : WordsContract.Presenter {

    init {
        this.mWordsView.setPresent(this)
    }

    override fun start() {
        loadWords(true)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (AddEditWordActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mWordsView.showSuccessfullySavedMessage()
        }
    }

    override fun loadWords(forceUpdate: Boolean) {
        loadWords(forceUpdate, true)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link WordsLocalContract}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadWords(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if(showLoadingUI) mWordsView.setLoadingIndicator(true)
        if(forceUpdate) mWordsRepository?.refreshWords()

        mWordsRepository?.getWords(object : WordsLocalContract.LoadWordsCallback {

            override fun onWordsLoaded(words: List<Word>) {
                if(!mWordsView.isActive()) return
                if(showLoadingUI) mWordsView.setLoadingIndicator(false)
                processWords(words)
            }

            override fun onDataNotAvailable() {
                if(!mWordsView.isActive()) return
                mWordsView.showLoadingWordsError()
            }

        })
    }

    private fun processWords(words: List<Word>) {
        if(words.isEmpty()) mWordsView.showNoWords()
        else mWordsView.showWords(words)
    }

    override fun addNewWord() {
        mWordsView.showAddWord()
    }

    override fun deleteWord(wordId: String) {
        mWordsRepository?.deleteWord(wordId)
        loadWords(true)
    }

}