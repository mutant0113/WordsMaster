package com.mutant.wordsmaster.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.common.base.Objects
import com.google.common.base.Strings
import java.util.*

/**
 * Immutable model class for a Task.
 */
@Entity(tableName = "words")
class Word(@PrimaryKey
           @ColumnInfo(name = "entryid")
           val id: String,
           @ColumnInfo(name = "title")
           val title: String,
           @ColumnInfo(name = "explanation")
           val explanation: String?,
           /**
            * Sentence making with keyword
            */
           @ColumnInfo(name = "eg")
           val eg: String?) {

    val titleForList: String
        get() = title

    val isEmpty: Boolean
        get() = Strings.isNullOrEmpty(title)

    /**
     * Use this constructor to create a new active Word.
     *
     * @param title       title of the word
     * @param explanation explanation of the word
     * @param eg          e.g. of the word
     */
    @Ignore
    constructor(title: String, explanation: String?, eg: String?) : this(UUID.randomUUID().toString(), title, explanation, eg)
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


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val word = o as Word?
        return Objects.equal(id, word?.id) &&
                Objects.equal(title, word?.title) &&
                Objects.equal(explanation, word?.explanation) &&
                Objects.equal(eg, word?.eg)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id, title, explanation, eg)
    }

    override fun toString(): String {
        return "Word with name " + title
    }
}
