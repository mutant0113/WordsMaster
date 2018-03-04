package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.content.Context
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRemoteContract
import com.mutant.wordsmaster.data.source.WordsRepository


class AddEditWordPresenter(private val context: Context,
                           private val mWordId: String?,
                           private val mWordsRepository: WordsRepository?,
                           private val mAddEditWordView: AddEditWordContract.View,
                           private val mShouldLoadDataFromRepo: Boolean) :
        AddEditWordContract.Present, WordsLocalContract.GetWordCallback {

    private var mIsDataMissing: Boolean = false

    init {
        mIsDataMissing = mShouldLoadDataFromRepo
        mAddEditWordView.setPresent(this)
    }

    override fun start() {
        if (!isEditMode() && mIsDataMissing) {
            populateWord()
        }
    }

    private fun isEditMode(): Boolean {
        return mWordId == null
    }

    override fun saveWord(title: String, explanation: String?, eg: String?) {
        if (isEditMode()) {
            createNewWord(title, explanation, eg)
        } else {
            updateWord(title, explanation, eg)
        }
    }

    private fun createNewWord(title: String, explanation: String?, eg: String?) {
        val word = Word(title, explanation, eg)
        if (word.isEmpty) {
            mAddEditWordView.showEmptyWordError()
        } else {
            mWordsRepository?.saveWord(word)
            mAddEditWordView.showWordsList()
        }
    }

    private fun updateWord(title: String, explanation: String?, eg: String?) {
        // TODO
    }

    override fun populateWord() {
        mWordsRepository?.getWordFromLocal(mWordId!!, this)
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

    override fun translate(activity: Activity, sourceText: String) {

//        Thread(Runnable {
//            val translate = TranslateOptions.newBuilder()
//                    .setApiKey("AIzaSyA3NIbkZXrty6xHMPxJ27-Zr73PtTqaTlI").build().service
//            val sourceLanguage = "en"
//            val targetLanguage = "zh-TW"
//            val sourceLanguageOption = Translate.TranslateOption.sourceLanguage(sourceLanguage)
//            val targetTranslateOption = Translate.TranslateOption.targetLanguage(targetLanguage)
//            val model = Translate.TranslateOption.model("nmt")
//
//            val translation = translate.translate(sourceText, sourceLanguageOption, targetTranslateOption, model)
//            translation.
//                mAddEditWordView.setExplanation(translation.translatedText)
//            }
//        }).start()
        mWordsRepository?.getWordFromGoogle(context, sourceText, object : WordsRemoteContract.GetWordCallback {

            override fun onWordLoaded(word: Word?) {
                // TODO
            }

            override fun onDataNotAvailable() {
                // TODO
            }

        })
    }



}
