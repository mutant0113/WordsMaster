package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.mutant.wordsmaster.MyApplication
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.SingleLiveEvent
import com.mutant.wordsmaster.data.source.WordsLocalContract
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.local.SettingsPreferences
import com.mutant.wordsmaster.data.source.model.Word

class AddEditWordViewModel(private val wordsRepository: WordsRepository) :
        AndroidViewModel(Application()), WordsLocalContract.GetWordCallback {

    private val _editMode = MutableLiveData<Boolean>()
    val editMode: LiveData<Boolean>
        get() = _editMode

    private val _word = MutableLiveData<Word>()
    val word: LiveData<Word>
        get() = _word

    private val _isNewWord = MutableLiveData<Boolean>()
    val isNewWord: LiveData<Boolean>
        get() = _isNewWord

    private val _showSoftKeyBoard = MutableLiveData<Boolean>()
    val showSoftKeyBoard: LiveData<Boolean>
        get() = _showSoftKeyBoard

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _dataLoadingError = MutableLiveData<Boolean>()
    val dataLoadingError: LiveData<Boolean>
        get() = _dataLoadingError

    private val _history = MutableLiveData<List<String>>()
    val history: LiveData<List<String>>
        get() = _history

    private val _snackBarStrId = SingleLiveEvent<Int>()
    val snackBarStrId: SingleLiveEvent<Int>
        get() = _snackBarStrId

    private val _showWordsListEvent = SingleLiveEvent<Unit>()
    val showWordsListEvent: SingleLiveEvent<Unit>
        get() = _showWordsListEvent

    private var wordTitle: String? = null

    fun start(wordTitle: String?) {
        _editMode.value = false
        this.wordTitle = wordTitle
        if (isSearching()) {
            this._showSoftKeyBoard.value = true
            return
        }
        getWordByTitle(wordTitle)
    }

    // Search a new word
    private fun isSearching(): Boolean {
        return wordTitle.isNullOrBlank()
    }

    fun getWordByTitle(wordTitle: String?) {
        this._dataLoading.value = true
        this._history.value = getHistory()
        wordsRepository.getWordByTitle(wordTitle!!, this)
    }

    override fun onWordLoaded(word: Word, isNewWord: Boolean) {
        this._word.value = word
        this._isNewWord.value = isNewWord
        this._showSoftKeyBoard.value = false
        this._dataLoading.value = false
        this._dataLoadingError.value = false
        saveHistory(word.title)
    }

    override fun onDataNotAvailable() {
        this._dataLoading.value = false
        this._dataLoadingError.value = true
    }

    private fun getHistory(): MutableList<String> {
        return SettingsPreferences.getHistory(MyApplication.context!!)
    }

    // TODO use live data for unit test?
    private fun saveHistory(wordTitle: String) {
        val wordsHistory = getHistory()
        wordsHistory.add(wordTitle)
        SettingsPreferences.setHistory(MyApplication.context!!, wordsHistory)
    }

    fun saveWord(word: Word) {
        insertOrUpdate(word)
    }

    private fun insertOrUpdate(word: Word) {
        if (word.isEmpty) {
            _snackBarStrId.value = R.string.empty_word_message
        } else {
            wordsRepository.saveWord(word)
            _showWordsListEvent.value = Unit
        }
    }

    fun translate(activity: Activity, sourceText: String) {
        Thread(Runnable {
            val translate = TranslateOptions.newBuilder()
                    .setApiKey("AIzaSyA3NIbkZXrty6xHMPxJ27-Zr73PtTqaTlI").build().service
            val sourceLanguage = "en"
            val targetLanguage = "zh-TW"
            val sourceLanguageOption = Translate.TranslateOption.sourceLanguage(sourceLanguage)
            val targetTranslateOption = Translate.TranslateOption.targetLanguage(targetLanguage)
            val model = Translate.TranslateOption.model("nmt")

            val translation = translate.translate(sourceText, sourceLanguageOption, targetTranslateOption, model)
        }).start()
    }
}