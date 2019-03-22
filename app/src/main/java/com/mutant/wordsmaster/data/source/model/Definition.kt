package com.mutant.wordsmaster.data.source.model

import androidx.room.ColumnInfo

data class Definition(
        // Part of speech
        @ColumnInfo(name = "pos")
        var pos: String?,

        @ColumnInfo(name = "def")
        var def: String?,

        @ColumnInfo(name = "example")
        var example: String?)