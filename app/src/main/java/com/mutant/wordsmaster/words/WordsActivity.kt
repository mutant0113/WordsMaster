package com.mutant.wordsmaster.words

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_words.*

class WordsActivity : AppCompatActivity() {

    companion object {
        const val TAG_FRAGMENT_WORDS = "WORDS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words)
//        setSupportActionBar(toolbar)

        initAds()

        var wordsFragment = supportFragmentManager.findFragmentById(R.id.content_main) as WordsFragment?
        if (wordsFragment == null) {
            wordsFragment = WordsFragment.newInstance()
            ActivityUtils.addFragment(supportFragmentManager, wordsFragment,
                    R.id.content_main, TAG_FRAGMENT_WORDS)
        }

        // TODO
//        // Load previously saved state, if available.
//        if (savedInstanceState != null) {
//            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
//            mTasksPresenter.setFiltering(currentFiltering)
//        }
    }

    private fun initAds() {
        val adRequest = AdRequest.Builder().build()
        ad_view.loadAd(adRequest)
        ad_view.adListener = mAdListener
    }

    private val mAdListener = object: AdListener() {

        override fun onAdLoaded() {
            // Code to be executed when an ad finishes loading.
        }

        override fun onAdFailedToLoad(errorCode : Int) {
            // Code to be executed when an ad request fails.
        }

        override fun onAdOpened() {
            // Code to be executed when an ad opens an overlay that
            // covers the screen.
        }

        override fun onAdLeftApplication() {
            // Code to be executed when the user has left the app.
        }

        override fun onAdClosed() {
            // Code to be executed when when the user is about to return
            // to the app after tapping on an ad.
        }
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
