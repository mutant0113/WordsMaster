package com.mutant.wordsmaster.words

import com.mutant.wordsmaster.data.source.model.Word

interface WordsListener {

    fun onNavigatorAddEditWordActivity(title: String)

    fun onExpandDef(word: Word)

    fun onTtsSpeak(title: String)
}