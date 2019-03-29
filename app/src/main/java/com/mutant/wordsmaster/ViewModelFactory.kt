package com.mutant.wordsmaster

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mutant.wordsmaster.addeditword.AddEditWordViewModel
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.util.Injection
import com.mutant.wordsmaster.words.WordsViewModel

/**
 * A creator is used to inject the product ID into the ViewModel
 *
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
class ViewModelFactory private constructor(private val wordsRepository: WordsRepository) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(WordsViewModel::class.java) ->
                        WordsViewModel(wordsRepository)
                    isAssignableFrom(AddEditWordViewModel::class.java) ->
                        AddEditWordViewModel(wordsRepository)
                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(applicationContext: Context) =
                INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                    INSTANCE ?: ViewModelFactory(
                            Injection.provideTasksRepository(applicationContext))
                            .also { INSTANCE = it }
                }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}