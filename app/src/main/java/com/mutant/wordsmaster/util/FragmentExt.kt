package com.mutant.wordsmaster.util

/**
 * Various extension functions for Fragment.
 */
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.mutant.wordsmaster.ViewModelFactory

fun <T : ViewModel> Fragment.obtainViewModel(viewModelClass: Class<T>) =
        ViewModelProviders.of(this,
                ViewModelFactory.getInstance(requireContext().applicationContext)).get(viewModelClass)