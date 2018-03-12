package com.mutant.wordsmaster.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class Tts(context: Context) {

    private lateinit var mTextToSpeech: TextToSpeech

    init {
        mTextToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
            if (it != TextToSpeech.ERROR) {
                // TODO change language
                mTextToSpeech.language = Locale.UK
            }
        })
    }

    companion object {

        fun newInstance(context: Context): Tts? {
            return Tts(context)
        }
    }

    fun speak(text: CharSequence) {
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun release() {
        mTextToSpeech.stop()
        mTextToSpeech.shutdown()
    }

}