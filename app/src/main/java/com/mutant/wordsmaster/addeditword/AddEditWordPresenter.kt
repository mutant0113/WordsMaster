package com.mutant.wordsmaster.addeditword

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.addeditword.contract.SearchWordContract
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRemoteContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.services.JsoupHelper
import com.mutant.wordsmaster.util.trace.DebugHelper


class AddEditWordPresenter(private val mWordId: String?,
                           private val mWordsRepository: WordsRepository,
                           private val mAddEditWordView: AddEditWordContract.View,
                           private val mSearchWordView: SearchWordContract.View,
                           private val mShouldLoadDataFromRepo: Boolean) :
        AddEditWordContract.Present, WordsLocalContract.GetWordCallback {

    private var mIsDataMissing: Boolean = false
    private var misWebViewLoaded = false

    init {
        mIsDataMissing = mShouldLoadDataFromRepo
        mAddEditWordView.setPresent(this)
        mSearchWordView.setPresent(this)
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun parseHtmlFromWebView(context: Context, sourceText: String) {
        mSearchWordView.showSearching()
        misWebViewLoaded = false

        val webView = WebView(context)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(LoadListener(), "HTMLOUT")
        webView.webViewClient = mWebViewClient
        // TODO srcLang and tgtLang must be parameters.
        webView.loadUrl("${JsoupHelper.getUrl()}#en/zh-TW/$sourceText")
    }ㄔㄛ

    private val mWebViewClient = object : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            if(!misWebViewLoaded) {
                view.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
                misWebViewLoaded = true
            }
        }
    }

    /**
     * We execute javascript to get html after website page loading finished.
     * Do not remove this code, it is not useless.
     */
    private inner class LoadListener {

        @JavascriptInterface
        fun processHTML(html: String) {
            DebugHelper.d(TAG, "processHTML: $html")
            mWordsRepository.parseHtml(html, object : WordsRemoteContract.GetWordCallback {
                override fun onWordLoaded(word: Word) {
                    setWordToView(word)
                }

                override fun onDataNotAvailable() {
                    DebugHelper.e(TAG, "processHTML: html parse to word failed.")
                    if(mSearchWordView.isActive())
                        mSearchWordView.showNoSuchWordError()
                }

            })
        }
    }

    private fun setWordToView(word: Word) {
        if(!mSearchWordView.isActive()) return
        if(!word.title.isNullOrBlank()) mAddEditWordView.setTitle(word.title)
        if(!word.explanation.isNullOrBlank()) mAddEditWordView.setExplanation(word.explanation)
        if(!word.eg.isNullOrBlank()) mAddEditWordView.setEg(word.eg)
        mSearchWordView.showWord()
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
    }

    companion object {
        private val TAG = AddEditWordPresenter::class.java.simpleName
    }

}
