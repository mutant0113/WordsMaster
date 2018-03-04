package com.mutant.wordsmaster.addeditword

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mutant.wordsmaster.data.Word
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRemoteContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.services.JsoupHelper
import com.mutant.wordsmaster.util.trace.DebugHelper


class AddEditWordPresenter(private val mWordId: String?,
                           private val mWordsRepository: WordsRepository,
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

    @SuppressLint("SetJavaScriptEnabled")
    override fun parseHtmlFromWebView(context: Context, sourceText: String) {
        val webView = WebView(context)

        webView.settings.javaScriptEnabled = true
        webView.clearHistory()
        webView.clearCache(true)
        webView.settings.builtInZoomControls = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.setSupportZoom(true)
        webView.settings.useWideViewPort = false
        webView.settings.loadWithOverviewMode = false
        webView.addJavascriptInterface(LoadListener(), "HTMLOUT")
        var isLoaded = false
        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                if(!isLoaded)
//                    Handler().postDelayed({ webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);"); }, 5000)
                    webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
            }
        }

        // TODO srcLang and tgtLang must be parameters.
        webView.loadUrl("${JsoupHelper.getUrl()}#en/zh-TW/$sourceText")
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
                }

            })
        }
    }

    private fun setWordToView(word: Word) {
        if(word.title.isNotEmpty()) mAddEditWordView.setTitle(word.title)
        if(word.explanation != null) mAddEditWordView.setExplanation(word.explanation)
        if(word.eg != null) mAddEditWordView.setEg(word.eg)
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
