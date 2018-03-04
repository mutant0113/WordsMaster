package com.mutant.wordsmaster.addeditword

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.services.JsoupHelper
import kotlinx.android.synthetic.main.fragment_addword.*
import kotlinx.android.synthetic.main.fragment_addword.view.*









class AddEditWordFragment : Fragment(), AddEditWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_addword, container, false)
        val fab = activity.findViewById(R.id.fab_edit_word_done) as FloatingActionButton

        fab.setOnClickListener { mPresenter?.saveWord(edit_text_title.text.toString(),
                edit_text_explanation.text.toString(), edit_text_eg.text.toString()) }

        root.edit_text_title.addTextChangedListener(mTitleTextWatcher)
        root.webView.settings.javaScriptEnabled = true
        root.webView.clearHistory();
        root.webView.clearCache(true)
        root.webView.settings.builtInZoomControls = true
        root.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        root.webView.settings.setSupportZoom(true)
        root.webView.settings.useWideViewPort = false
        root.webView.settings.loadWithOverviewMode = false
        root.webView.settings.userAgentString =  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"
        root.webView.addJavascriptInterface(LoadListener(), "HTMLOUT")
        root.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
//                Handler().postDelayed({ root.webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);"); }, 5000)
                root.webView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
            }
        }
        root.webView.loadUrl("https://translate.google.com/#en/zh-TW/hello")
        return root
    }

    internal inner class LoadListener {

        @JavascriptInterface
        fun processHTML(html: String) {
            Log.i("result", html)
            JsoupHelper.parseHtml(html)
        }
    }

    private val mTitleTextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // TODO use button instead of translating immediately
            mPresenter?.translate(activity, s.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        mPresenter?.start()
    }

    companion object {
        const val ARGUMENT_EDIT_WORD_ID = "EDIT_WORD_ID"

        fun newInstance(): AddEditWordFragment {
            return AddEditWordFragment()
        }
    }

    override fun setPresent(present: AddEditWordPresenter) {
        this.mPresenter = present
    }

    override fun showEmptyWordError() {
        Snackbar.make(edit_text_title, getString(R.string.empty_word_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showWordsList() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setTitle(title: String) {
        edit_text_title.setText(title)
    }

    override fun setExplanation(explanation: String?) {
        edit_text_explanation.setText(explanation)
    }

    override fun setEg(eg: String?) {
        edit_text_eg.setText(eg)
    }

    override fun isActive(): Boolean {
        return view != null
    }
}