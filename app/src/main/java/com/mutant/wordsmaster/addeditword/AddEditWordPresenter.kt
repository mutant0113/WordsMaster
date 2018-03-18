package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.content.Context
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.addeditword.contract.SearchWordContract
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.model.Word


class AddEditWordPresenter(private val mContext: Context,
                           private val mWordTitle: String?,
                           private val mWordsRepository: WordsRepository,
                           private val mAddEditWordView: AddEditWordContract.View,
                           private val mSearchWordView: SearchWordContract.View) :
        AddEditWordContract.Present, WordsLocalContract.GetWordCallback {

    init {
        mAddEditWordView.setPresent(this)
        mSearchWordView.setPresent(this)
    }

    override fun start() {
        mAddEditWordView.setEditMode(false)
        if (!isSearching()) {
            populateWord()
        } else {
            mSearchWordView.showKeyboard()
        }
    }

    private fun isSearching(): Boolean {
        return mWordTitle.isNullOrBlank()
    }

    override fun saveWord(word: Word) {
        insertOrUpdate(word)
    }

    private fun insertOrUpdate(word: Word) {
        if (word.isEmpty) {
            mAddEditWordView.showEmptyWordError()
        } else {
            mWordsRepository.saveWord(word)
            mAddEditWordView.showWordsList()
        }
    }

    override fun populateWord() {
        if (!mWordTitle.isNullOrBlank())
            mWordsRepository.getWordByTitle(mContext, mWordTitle, this)
    }

    override fun getWordByTitle(wordTitle: String?) {
        mSearchWordView.showSearching()
        mWordsRepository.getWordByTitle(mContext, wordTitle, this)
    }

    override fun onWordLoaded(word: Word, isNewWord: Boolean) {
        if (!mAddEditWordView.isActive()) return
        if (!mSearchWordView.isActive()) return
        mAddEditWordView.setView(word)
        mSearchWordView.showWord(isNewWord)
    }

    override fun onDataNotAvailable() {
        if (!mSearchWordView.isActive()) return
        mSearchWordView.showNoSuchWordError()

        if (!mAddEditWordView.isActive()) return
        mAddEditWordView.showEmptyWordError()
    }

    override fun translate(activity: Activity, sourceText: String) {
        Thread(Runnable {
            val translate = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyA3NIbkZXrty6xHMPxJ27-Zr73PtTqaTlI").build().service
            val sourceLanguage = "en"
            val targetLanguage = "zh-TW"
            val sourceLanguageOption = Translate.TranslateOption.sourceLanguage(sourceLanguage)
            val targetTranslateOption = Translate.TranslateOption.targetLanguage(targetLanguage)
            val model = Translate.TranslateOption.model("nmt")

            val translation = translate.translate(sourceText, sourceLanguageOption, targetTranslateOption, model)
        }
        ).start()
    }

    companion object {
        private val TAG = AddEditWordPresenter::class.java.simpleName
    }

}
