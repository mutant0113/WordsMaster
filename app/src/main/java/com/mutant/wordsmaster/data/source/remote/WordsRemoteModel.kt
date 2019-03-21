package com.mutant.wordsmaster.data.source.remote

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRemoteContract
import com.mutant.wordsmaster.services.JsoupHelper
import com.mutant.wordsmaster.util.AppExecutors
import com.mutant.wordsmaster.util.trace.DebugHelper

class WordsRemoteModel
private constructor(private val mAppExecutors: AppExecutors) : WordsRemoteContract {

    private var misWebViewLoaded = false
    private var mCallback: WordsLocalContract.GetWordCallback? = null

    override fun getWordByTitle(context: Context, wordTitle: String?, callback: WordsLocalContract.GetWordCallback) {
        mCallback = callback
        parseHtmlFromWebView(context, wordTitle)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun parseHtmlFromWebView(context: Context, sourceText: String?) {
        misWebViewLoaded = false
        WebView(context).apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(LoadListener(), "HTMLOUT")
            webViewClient = mWebViewClient
        }.run {
            // TODO srcLang and tgtLang must be parameters.
            loadUrl("${JsoupHelper.getUrl()}#en/zh-TW/$sourceText")
            DebugHelper.d(msg = "${JsoupHelper.getUrl()}#en/zh-TW/$sourceText")
        }
    }

    private val mWebViewClient = object : WebViewClient() {

        override fun onPageFinished(view: WebView, url: String) {
            if (!misWebViewLoaded) {
                view.loadUrl("javascript:HTMLOUT.processHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                DebugHelper.d(msg = "onPageFinished, loadUrl HTMLOUT")
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
        @SuppressWarnings("unused")
        fun processHTML(html: String) {
            // TODO
            DebugHelper.d(msg = "processHTML: $html")
            parseHtml(html)
        }
    }

    fun parseHtml(html: String) {
        val runnable = Runnable {
            val word = JsoupHelper.parseHtml(html)
            mAppExecutors.mainThread().execute {
                if (word == null || word.definitions.size == 0) {
                    mCallback?.onDataNotAvailable()
                } else {
                    mCallback?.onWordLoaded(word, true)
                }
            }
        }

        mAppExecutors.networkIO().execute(runnable)
    }

//    override fun getWordById(context: Context, sourceText: String, callback: WordsRemoteContract.GetWordCallback) {
////        ApiClient.getInstance().getRawHtml("en", "zh-TW", "hello", TranslateCallback(callback))
//        val runnable = Runnable {
//            val word = parseHtmlToWord(context, sourceText)
//            mAppExecutors.mainThread().execute({
//                if (word == null) {
//                    callback.onDataNotAvailable()
//                } else {
//                    callback.onWordLoaded(word)
//                }
//            })
//        }
//
//        mAppExecutors.networkIO().execute(runnable)
//    }

    companion object {

        private var INSTANCE: WordsRemoteModel? = null

        fun getInstance(appExecutors: AppExecutors): WordsRemoteContract? {
            if (WordsRemoteModel.INSTANCE == null) {
                synchronized(WordsLocalContract::class.java) {
                    if (WordsRemoteModel.INSTANCE == null) {
                        WordsRemoteModel.INSTANCE = WordsRemoteModel(appExecutors)
                    }
                }
            }
            return WordsRemoteModel.INSTANCE
        }
    }
}