package com.mutant.wordsmaster.addeditword

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.search.SearchWordFragment
import com.mutant.wordsmaster.util.ActivityUtils
import com.mutant.wordsmaster.util.DialogUnits
import com.mutant.wordsmaster.util.obtainViewModel

class AddEditWordActivity : AppCompatActivity() {

    private lateinit var mAddEditWordFragment: AddEditWordFragment
    private lateinit var viewModel: AddEditWordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addword)

        val wordTitle = intent.getStringExtra(AddEditWordFragment.ARGUMENT_WORD_TITLE)

        var searchFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as SearchWordFragment?
        if (searchFragment == null) {
            searchFragment = SearchWordFragment.newInstance()

            ActivityUtils.addFragment(supportFragmentManager, searchFragment,
                    R.id.content_frame, TAG_FRAGMENT_SEARCH_WORD)
        }

        mAddEditWordFragment = AddEditWordFragment.newInstance()
        if (wordTitle != null) {
            val bundle = Bundle()
            bundle.putString(AddEditWordFragment.ARGUMENT_WORD_TITLE, wordTitle)
            mAddEditWordFragment.arguments = bundle
        }
        // TODO Getting data means this page is in edit mode

        ActivityUtils.addFragment(supportFragmentManager, mAddEditWordFragment,
                R.id.content_frame, TAG_FRAGMENT_ADDEDITWORD)

        if (wordTitle.isNullOrBlank())
            ActivityUtils.hideFragment(supportFragmentManager, TAG_FRAGMENT_ADDEDITWORD)
        else
            ActivityUtils.hideFragment(supportFragmentManager, TAG_FRAGMENT_SEARCH_WORD)

        viewModel = obtainViewModel()
    }

    override fun onResume() {
        super.onResume()
        obtainViewModel().start(intent.getStringExtra(AddEditWordFragment.ARGUMENT_WORD_TITLE))
    }

    fun obtainViewModel(): AddEditWordViewModel = obtainViewModel(AddEditWordViewModel::class.java)

    fun showWord(isEditMode: Boolean) {
        mAddEditWordFragment.setEditMode(isEditMode)
        ActivityUtils.switchFragment(supportFragmentManager, TAG_FRAGMENT_ADDEDITWORD)
    }

    override fun onBackPressed() {
        if (viewModel.editMode.value == true) {
            DialogUnits.createDialog(this, "Changes not save",
                    "Are you sure to leave without saving?",
                    DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() },
                    DialogInterface.OnClickListener { _, _ -> super.onBackPressed() }).show()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val REQUEST_ADD_WORD = 1
        const val TAG_FRAGMENT_ADDEDITWORD = "ADDEDITWORD"
        const val TAG_FRAGMENT_SEARCH_WORD = "SEARCHWORD"

        fun getIntent(context: Context, wordTitle: String): Intent {
            val intent = Intent(context, AddEditWordActivity::class.java)
            intent.putExtra(AddEditWordFragment.ARGUMENT_WORD_TITLE, wordTitle)
            return intent
        }
    }
}