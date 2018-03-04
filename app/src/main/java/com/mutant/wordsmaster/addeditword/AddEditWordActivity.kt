package com.mutant.wordsmaster.addeditword

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.util.ActivityUtils
import com.mutant.wordsmaster.util.Injection

class AddEditWordActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_ADD_TASK = 1
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
        const val TAG_FRAGMENT_ADDEDITWORD = "ADDEDITWORD"
        const val TAG_FRAGMENT_SEARCH_WORD = "SEARCHWORD"
    }

    private lateinit var mAddEditWordPresenter: AddEditWordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addword)

        var searchFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as SearchWordFragment?
        val wordId = intent.getStringExtra(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID)

        if (searchFragment == null) {
            // Create the fragment
            searchFragment = SearchWordFragment.newInstance()

            // TODO Getting data means this page is in edit mode
            if (intent.hasExtra(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID)) {
//                val bundle = Bundle()
//                bundle.putString(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID, wordId)
//                searchWordFragment.arguments = bundle
            }

            ActivityUtils.addFragment(supportFragmentManager, searchFragment,
                    R.id.content_frame, TAG_FRAGMENT_SEARCH_WORD)
        }

        // Create the fragment
        val addEditWordFragment = AddEditWordFragment.newInstance()

        // TODO Getting data means this page is in edit mode
        if (intent.hasExtra(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID)) {
            val bundle = Bundle()
            bundle.putString(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID, wordId)
            addEditWordFragment.arguments = bundle
        }

        ActivityUtils.addFragment(supportFragmentManager, addEditWordFragment,
                R.id.content_frame, TAG_FRAGMENT_ADDEDITWORD)
        ActivityUtils.hideFragment(supportFragmentManager, TAG_FRAGMENT_ADDEDITWORD)

        var shouldLoadDataFromRepo = true

        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY)
        }

        // Create the presenter
        mAddEditWordPresenter = AddEditWordPresenter(wordId,
                Injection.provideTasksRepository(applicationContext), addEditWordFragment,
                searchFragment, shouldLoadDataFromRepo)
    }

    fun showWord() {
        ActivityUtils.switchFragment(supportFragmentManager, TAG_FRAGMENT_ADDEDITWORD)
    }

}