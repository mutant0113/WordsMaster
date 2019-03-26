package com.mutant.wordsmaster.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mutant.wordsmaster.SingleLiveEvent
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.model.Word

class WordsViewModel(private val WordsRepository: WordsRepository) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>().apply { value = emptyList() }
    val words: LiveData<List<Word>>
        get() = _words

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _dataLoadingError = MutableLiveData<Boolean>()
    val dataLoadingError: LiveData<Boolean>
        get() = _dataLoadingError

    val empty: LiveData<Boolean> = Transformations.map(words) {
        it.isEmpty()
    }

    private val _openAddEditWordEvent = SingleLiveEvent<String>()
    val openAddEditWordEvent: SingleLiveEvent<String>
        get() = _openAddEditWordEvent

    private val _ttsTitle = MutableLiveData<String>()
    val ttsTitle: LiveData<String>
        get() = _ttsTitle

    fun start() {
        loadWords(forceUpdate = true, showLoadingUI = true)
    }

    fun replaceData(words: List<Word>) {
        this._words.value = words
    }

    /**
     * Called by the [WordsAdapter].
     */
    internal fun openAddEditWordActivity(title: String) {
        _openAddEditWordEvent.value = title
    }

    fun setTtsTitle(title: String) {
        this._ttsTitle.value = title
    }

    private fun loadWords(forceUpdate: Boolean, showLoadingUI: Boolean) {
        _dataLoading.value = showLoadingUI
        if (forceUpdate) WordsRepository.refreshWords()

        WordsRepository.getWords(object : WordsLocalContract.LoadWordsCallback {

            override fun onWordsLoaded(words: MutableList<Word>) {
                _dataLoading.value = false
                _dataLoadingError.value = false
                replaceData(words)
            }

            override fun onDataNotAvailable() {
                _dataLoading.value = false
                _dataLoadingError.value = true
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