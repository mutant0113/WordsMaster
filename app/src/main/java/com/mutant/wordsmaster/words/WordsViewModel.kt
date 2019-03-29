package com.mutant.wordsmaster.words

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.SingleLiveEvent
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.model.Word

class WordsViewModel(private val WordsRepository: WordsRepository) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>().apply { value = emptyList() }
    val words: LiveData<List<Word>>
        get() = _words

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean>
        get() = _empty

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _openAddEditWordEvent = SingleLiveEvent<String>()
    val openAddEditWordEvent: SingleLiveEvent<String>
        get() = _openAddEditWordEvent

    private val _snackBarStrId = SingleLiveEvent<Int>()
    val snackBarStrId: SingleLiveEvent<Int>
        get() = _snackBarStrId

    private val _ttsWord = SingleLiveEvent<String>()
    val ttsWord: SingleLiveEvent<String>
        get() = _ttsWord

    fun start() {
        loadWords(forceUpdate = true, showLoadingUI = true)
    }

    fun replaceData(words: List<Word>) {
        this._words.value = words
        this._empty.value = words.isEmpty()
    }

    /**
     * Called by the [WordsAdapter].
     */
    internal fun openAddEditWordActivity(title: String = "") {
        _openAddEditWordEvent.value = title
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (AddEditWordActivity.REQUEST_ADD_WORD == requestCode && Activity.RESULT_OK == resultCode) {
            _snackBarStrId.value = R.string.successfully_saved_word_message
        }
    }

    fun setTtsWord(word: String) {
        this._ttsWord.value = word
    }

    private fun loadWords(forceUpdate: Boolean, showLoadingUI: Boolean) {
        _dataLoading.value = showLoadingUI
        if (forceUpdate) WordsRepository.refreshWords()

        WordsRepository.getWords(object : WordsLocalContract.LoadWordsCallback {

            override fun onWordsLoaded(words: MutableList<Word>) {
                _dataLoading.value = false
                replaceData(words)
            }

            override fun onDataNotAvailable() {
                _dataLoading.value = false
                _snackBarStrId.value = R.string.loading_words_error
            }

        })
    }

    fun deleteWord(wordId: String) {
        WordsRepository.deleteWord(wordId)
    }

    fun swap(word1: Word, word2: Word) {
        WordsRepository.swap(word1, word2)
    }
}