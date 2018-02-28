package com.mutant.wordsmaster.words

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.util.ActivityUtils
import com.mutant.wordsmaster.util.Injection

class WordsActivity : AppCompatActivity() {

    private lateinit var mWordsPresent: WordsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)
//        setSupportActionBar(toolbar)

        var wordsFragment = supportFragmentManager.findFragmentById(R.id.content_main) as WordsFragment?
        if (wordsFragment == null) {
            // Create the fragment
            wordsFragment = WordsFragment.newInstance()
            ActivityUtils.addFragmentToActivity(supportFragmentManager, wordsFragment, R.id.content_main)
        }

        // Create the presenter
        mWordsPresent = WordsPresenter(
                Injection.provideTasksRepository(applicationContext), wordsFragment)

        // TODO
//        // Load previously saved state, if available.
//        if (savedInstanceState != null) {
//            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
//            mTasksPresenter.setFiltering(currentFiltering)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
