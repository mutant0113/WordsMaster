package com.mutant.wordsmaster.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.words.WordsAdapter

object DataBindingMethods {

    @BindingAdapter("app:goneUnless")
    @JvmStatic
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, words: MutableList<Word>) {
        with(recyclerView.adapter as WordsAdapter) {
            replaceData(words)
        }
    }

    @BindingAdapter("app:softKeyBoard")
    @JvmStatic
    fun softKeyBoard(view: View, active: Boolean) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (active) {
            view.requestFocus()
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        } else {

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}