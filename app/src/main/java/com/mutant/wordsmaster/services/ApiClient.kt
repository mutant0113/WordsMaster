package com.mutant.wordsmaster.services

import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient private constructor() : ApiContract {

    private var mRetrofit: Retrofit

    init {
        mRetrofit = Retrofit.Builder()
                .baseUrl(HttpUrl.parse(GOOGLE_TRANSLATE_URL))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
    }

    override fun getRawHtml(srcLang: String, tgtLang: String, word: String, callback: Callback<ResponseBody>) {
        val apiService = mRetrofit.create(ApiService::class.java)
        val rawHtml = apiService.getRawHtml(srcLang, tgtLang, word)
        rawHtml.enqueue(callback)
    }

    companion object {
        var TAG = ApiClient::class.java.simpleName!!
        private const val GOOGLE_TRANSLATE_URL = "https://translate.google.com/"
        private var mApiClient: ApiClient? = null

        fun getInstance(): ApiClient {
            if (mApiClient == null) {
                mApiClient = ApiClient()
            }
            return mApiClient!!
        }
    }

}