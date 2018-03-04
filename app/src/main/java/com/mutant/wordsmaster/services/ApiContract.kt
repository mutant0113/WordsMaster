package com.mutant.wordsmaster.services

import okhttp3.ResponseBody
import retrofit2.Callback

interface ApiContract {

    interface LoadHtmlCallback {

        fun onHtmlLoaded(html: String)

        fun onDataNotAvailable()
    }

    fun getRawHtml(srcLang: String, tgtLang: String, word: String, callback: Callback<ResponseBody>)

}