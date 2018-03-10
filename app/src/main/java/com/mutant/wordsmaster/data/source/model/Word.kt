package com.mutant.wordsmaster.data.source.model

import android.arch.persistence.room.*
import com.google.common.base.Strings
import java.util.*

/**
 * Immutable model class for a Task.
 */
@Entity(tableName = "words")
data class Word(
        @PrimaryKey
        @ColumnInfo(name = "entryid")
        val id: String,

        @ColumnInfo(name = "title")
        var title: String,

        @TypeConverters(DefConverter::class)
        @ColumnInfo(name = "definitions")
        var definitions: List<Definition>?,

        /**
         * Sentence making with keyword
         */
        @ColumnInfo(name = "example")
        var example: String?) {

    val titleForList: String
        get() = title

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title)

    /**
     * Use this constructor to create a new active Word.
     *
     * @param title      title of the word
     * @param definition Definition of the word
     * @param example    e.g. of the word
     */
    @Ignore
    constructor(title: String, definition: List<Definition>?, example: String?) : this(UUID.randomUUID().toString(), title, definition, example)

    //
//    /**
//     * Use this constructor to create an active Task if the Task already has an id (copy of another
//     * Task).
//     *
//     * @param title       title of the task
//     * @param description description of the task
//     * @param id          id of the task
//     */
//    @Ignore
//    constructor(id: String, title: String?, description: String?) : this(id, title, description)
    @Ignore
    constructor() : this(UUID.randomUUID().toString(), "", arrayListOf(), "")

}
