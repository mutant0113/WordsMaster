package com.mutant.wordsmaster.data.source.remote

import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRemoteContract
import com.mutant.wordsmaster.services.JsoupHelper
import com.mutant.wordsmaster.util.AppExecutors

class WordsRemoteModel
private constructor(private val mAppExecutors: AppExecutors) : WordsRemoteContract {

    override fun parseHtml(html: String, callback: WordsRemoteContract.GetWordCallback) {
        val runnable = Runnable {
            val word = JsoupHelper.parseHtml(html)
            mAppExecutors.mainThread().execute({
                if (word == null || word.definitionsJson.isNullOrBlank() || word.example.isNullOrBlank()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onWordLoaded(word)
                }
            })
        }

        mAppExecutors.networkIO().execute(runnable)
    }

//    override fun getWord(context: Context, sourceText: String, callback: WordsRemoteContract.GetWordCallback) {
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