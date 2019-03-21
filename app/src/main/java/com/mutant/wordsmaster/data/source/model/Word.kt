package com.mutant.wordsmaster.data.source.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.common.base.Strings
import java.util.*

/**
 * Immutable model class for a Task.
 */
@Entity(tableName = "words")
data class Word(
        @PrimaryKey
        @ColumnInfo(name = "entryid")
        var id: String,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "definitionsJson")
        var definitions: MutableList<Definition>,

        /**
         * Sentence making with keyword
         */
        @ColumnInfo(name = "example")
        var examples: MutableList<String>) {

    val titleForList: String
        get() = title

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title)

    /**
     * Use this constructor to create a new active Word.
     *
     * @param title      title of the word
     * @param definitions Definition of the word
     * @param examples    e.g. of the word
     */
    @Ignore
    constructor(title: String, definitions: MutableList<Definition>, examples: MutableList<String>) :
            this(UUID.randomUUID().toString(), title, definitions, examples)

    @Ignore
    constructor() : this(UUID.randomUUID().toString(), "", mutableListOf<Definition>(), mutableListOf<String>())
}