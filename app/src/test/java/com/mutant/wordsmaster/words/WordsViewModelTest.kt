package com.mutant.wordsmaster.words

import android.app.Activity.RESULT_OK
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mutant.wordsmaster.LiveDataTestUtil
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.source.WordsLocalContract.LoadWordsCallback
import com.mutant.wordsmaster.data.source.WordsRepository
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.capture
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the implementation of [WordsViewModel]
 */
class WordsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var wordsRepository: WordsRepository

    @Captor
    private lateinit var loadWordsCallbackCaptor: ArgumentCaptor<LoadWordsCallback>

    private lateinit var wordsViewModel: WordsViewModel
    private val words = arrayListOf<Word>().apply {
        add(Word("Title1", mutableListOf(), mutableListOf()))
        add(Word("Title2", mutableListOf(), mutableListOf()))
        add(Word("Title3", mutableListOf(), mutableListOf()))
    }

    @Before
    fun setupViewModel() {
        MockitoAnnotations.initMocks(this)
        wordsViewModel = WordsViewModel(wordsRepository)
    }

    @Test
    fun loadAllWordsFromRepository_dataLoaded() {
        // Given an initialized WordsViewModel with initialized words
        // When loading of words is requested
        wordsViewModel.start()

        // Callback is captured and invoked with stubbed words
        verify<WordsRepository>(wordsRepository).getWords(capture(loadWordsCallbackCaptor))

        // Then progress indicator is shown
        assertThat(wordsViewModel.dataLoading.value, `is`(true))
        loadWordsCallbackCaptor.value.onWordsLoaded(words)

        // Then progress indicator is hidden
        assertThat(wordsViewModel.dataLoading.value, `is`(false))

        // And the error message won't show
        val value = LiveDataTestUtil.getValue(wordsViewModel.snackBarStrId)
        assertThat(wordsViewModel.snackBarStrId.hasBeenHandled.get(), `is`(true))
        assertThat(value, `is`(nullValue()))

        // And data loaded
        assertThat(wordsViewModel.words.value?.size, `is`(3))
        val valueEmpty = LiveDataTestUtil.getValue(wordsViewModel.empty)
        assertThat(valueEmpty, `is`(false))
    }

    @Test
    fun loadAllWordsFromRepository_dataLoadedFailed() {
        // Given an initialized WordsViewModel
        // When loading of words is requested
        wordsViewModel.start()

        // Callback is captured and invoked with stubbed words
        verify<WordsRepository>(wordsRepository).getWords(capture(loadWordsCallbackCaptor))

        // Then progress indicator is shown
        assertThat(wordsViewModel.dataLoading.value, `is`(true))
        loadWordsCallbackCaptor.value.onDataNotAvailable()

        // Then progress indicator is hidden
        assertThat(wordsViewModel.dataLoading.value, `is`(false))

        // And the error message is shown
        val value = LiveDataTestUtil.getValue(wordsViewModel.snackBarStrId)
        assertThat(value, `is`(R.string.loading_words_error))

        // And empty livedata won't invoke
        assertThat(wordsViewModel.empty.value, `is`(nullValue()))
    }

    @Test
    @Throws(InterruptedException::class)
    fun clickOnCardItem_showsAddTaskUi() {
        // When opening AddEditWordActivity
        val title = "Title"
        wordsViewModel.openAddEditWordActivity(title)
        assertThat(wordsViewModel.openAddEditWordEvent.hasBeenHandled.get(), `is`(false))

        // Then the event is triggered
        val value = LiveDataTestUtil.getValue(wordsViewModel.openAddEditWordEvent)
        assertThat(wordsViewModel.openAddEditWordEvent.hasBeenHandled.get(), `is`(true))
        assertThat(value, `is`(title))
    }

    @Test
    @Throws(InterruptedException::class)
    fun handleActivityResult_addWordOK() {
        // When AddEditWordActivity sends an REQUEST_ADD_WORD and RESULT_OK
        wordsViewModel.handleActivityResult(AddEditWordActivity.REQUEST_ADD_WORD, RESULT_OK)
        assertThat(wordsViewModel.snackBarStrId.hasBeenHandled.get(), `is`(false))

        // Then the snackBar shows the correct message
        val value = LiveDataTestUtil.getValue(wordsViewModel.snackBarStrId)
        assertThat(wordsViewModel.snackBarStrId.hasBeenHandled.get(), `is`(true))
        assertThat(value, `is`(R.string.successfully_saved_word_message))
    }

    @Test
    @Throws(InterruptedException::class)
    fun clickOnPronImage_ttsSpeak() {
        // When user clicks the imageView of the pronunciation
        val word = "Word"
        wordsViewModel.setTtsWord(word)
        assertThat(wordsViewModel.ttsWord.hasBeenHandled.get(), `is`(false))

        // Then the tts speaks the title
        val value = LiveDataTestUtil.getValue(wordsViewModel.ttsWord)
        assertThat(wordsViewModel.ttsWord.hasBeenHandled.get(), `is`(true))
        assertThat(value, `is`(word))
    }
}