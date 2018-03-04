package com.mutant.wordsmaster.services

import android.content.Context
import android.util.Log
import com.mutant.wordsmaster.data.Word
import org.jsoup.Jsoup



class JsoupHelper {

    companion object {

        var TAG = JsoupHelper::class.java.simpleName!!
        private const val GOOGLE_TRANSLATE_URL = "https://translate.google.com/"
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"

        fun getRawHtml(context: Context, srcLang: String, tgtLang: String, word: String): Word? {
            try {
                val completedUrl = "$GOOGLE_TRANSLATE_URL#$srcLang/$tgtLang/$word"
                val document = Jsoup.connect(completedUrl)
                        .userAgent(USER_AGENT)
                        .maxBodySize(0)
                        .timeout(600000)
                        .get()
                val elements = document.select("div.gt-cd-pos")
                Log.i("test", elements[0].text())
            } catch (e: Exception) {
                Log.i("test", e.toString())
            }
            return null
        }

        fun parseHtml(rawHtml: String): Word? {
            val document = Jsoup.parse(rawHtml)
            val elements = document.select("div.gt-cd-pos")
            Log.i("result", elements[0].text())
            return null
        }
    }

}