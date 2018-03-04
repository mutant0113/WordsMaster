package com.mutant.wordsmaster.services

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    /**
     * @GET("#en/zh-TW/hello")
     */
    @GET("#{srcLang}/{tgtLang}/{word}")
    fun getRawHtml(@Path("srcLang") srcLang: String,
                   @Path("tgtLang") tgtLang: String,
                   @Path("word") word: String): Call<ResponseBody>
}