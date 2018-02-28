package com.mutant.wordsmaster.addeditword

import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsDataSource

class AddEditWordPresenter(private val mWordId: String?,
                           private val mWordsRepository: WordsDataSource,
                           private val mAddEditWordView: AddEditWordContract.View,
                           private val mShouldLoadDataFromRepo: Boolean) :
        AddEditWordContract.Present, WordsDataSource.GetWordCallback {

    private var mIsDataMissing: Boolean = false

    init {
        mIsDataMissing = mShouldLoadDataFromRepo
        mAddEditWordView.setPresent(this)
    }

    override fun start() {
        if (!isNewWord() && mIsDataMissing) {
            populateWord()
        }
    }

    private fun isNewWord(): Boolean {
        return mWordId == null
    }

    override fun saveWord(title: String, explanation: String?, eg: String?) {
        if(isNewWord()) {
            createNewWord(title, explanation, eg)
        } else {
            updateWord(title, explanation, eg)
        }
    }

    private fun createNewWord(title: String, explanation: String?, eg: String?) {
        val word = Word(title, explanation, eg)
        if(word.isEmpty) {
            mAddEditWordView.showEmptyWordError()
        } else {
            mWordsRepository.saveWord(word)
            mAddEditWordView.showWordsList()
        }
    }

    private fun updateWord(title: String, explanation: String?, eg: String?) {

    }

    override fun populateWord() {
        mWordsRepository.getWord(mWordId!!, this)
    }

    override fun onWordLoaded(word: Word) {
        if (!mAddEditWordView.isActive()) return
        mAddEditWordView.setTitle(word.title)
        mAddEditWordView.setExplanation(word.explanation)
        mAddEditWordView.setEg(word.eg)
        mIsDataMissing = false
    }

    override fun onDataNotAvailable() {
        if (!mAddEditWordView.isActive()) return
        mAddEditWordView.showEmptyWordError()
    }

    override fun isDataMissing(): Boolean {
        return mIsDataMissing
    }
}