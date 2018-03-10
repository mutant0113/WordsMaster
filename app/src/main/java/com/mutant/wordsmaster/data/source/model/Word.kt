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
        val id: String,

        @ColumnInfo(name = "title")
        var title: String,

        @ColumnInfo(name = "definitionsJson")
        var definitionsJson: String?,

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
    constructor(title: String, definitions: List<Definition>?, example: String?) :
            this(UUID.randomUUID().toString(), title, DefConverter.toJson(definitions), example)

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
    constructor() : this(UUID.randomUUID().toString(), "", "", "")

}
