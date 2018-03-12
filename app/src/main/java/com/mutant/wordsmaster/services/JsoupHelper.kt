package com.mutant.wordsmaster.services

import android.util.Log
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class JsoupHelper {

    companion object {

        private val TAG = JsoupHelper::class.java.simpleName
        private const val GOOGLE_TRANSLATE_URL = "https://translate.google.com/"
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36"

        fun getUrl(): String = GOOGLE_TRANSLATE_URL

        /**
         * Connect to google translate website and get raw html. Then parse it into Word(POJO).
         * But we face a big problem here is that JSoup won't wait javascript to execute, so we'll miss some html code.
         */
        fun getRawHtml(srcLang: String, tgtLang: String, word: String): Word? {
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

        /**
         * Only parse html into Word(POJO).
         */
        fun parseHtml(html: String): Word? {
            val document = Jsoup.parse(html)
            val word = Word()
            parseTitle(word, document.select(Selector.Title.outer))
            parseDefinition(word, document.select(Selector.Definition.outer))
            parseExample(word, document.select(Selector.Example.outer))
            return if (word.title.isBlank() || word.definitions.isEmpty()) null else word
        }

        private fun parseTitle(word: Word, outer: Elements?) {
            val title = outer?.get(1)?.select(Selector.Title.title)?.text()
            word.title = title ?: ""
        }

        private fun parseDefinition(word: Word, outer: Elements?) {
            val engDef = outer?.get(1)
            val size = engDef?.select(Selector.Definition.cdPos)?.size ?: 0
            val definitions = arrayListOf<Definition>()
            for (i in 0 until size) {
                val pos = engDef?.select(Selector.Definition.cdPos)?.get(i)?.text()
                val def = engDef?.select(Selector.Definition.defRow)?.get(i)?.text()
                val example = engDef?.select(Selector.Definition.defExample)?.get(i)?.text()
                var definition = Definition(pos, def, example)
                definitions.add(definition)
            }
            word.definitions = definitions
        }

        private fun parseExample(word: Word, outer: Elements?) {
            val size = outer?.size ?: 0
            val examples = arrayListOf<String>()
            for (i in 0 until size) {
                examples.add(outer?.get(i)?.text() ?: "")
            }
            word.examples = examples
        }
    }

}