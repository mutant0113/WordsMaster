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
    }

    private lateinit var mAddEditWordPresenter: AddEditWordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addword)

        var addEditWordFragment = supportFragmentManager.findFragmentById(R.id.content_edit) as AddEditWordFragment?
        val wordId = intent.getStringExtra(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID)

        if (addEditWordFragment == null) {
            // Create the fragment
            addEditWordFragment = AddEditWordFragment.newInstance()

            if (intent.hasExtra(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID)) {
                val bundle = Bundle()
                bundle.putString(AddEditWordFragment.ARGUMENT_EDIT_WORD_ID, wordId)
                addEditWordFragment.arguments = bundle
            }

            ActivityUtils.addFragmentToActivity(supportFragmentManager,
                    addEditWordFragment, R.id.content_edit)
        }

        var shouldLoadDataFromRepo = true

        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY)
        }

        // Create the presenter
        mAddEditWordPresenter = AddEditWordPresenter(wordId,
                Injection.provideTasksRepository(applicationContext), addEditWordFragment, shouldLoadDataFromRepo)

    }

}