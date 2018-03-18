package com.mutant.wordsmaster.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class Utils {

    companion object {

        fun showKeyBoard(context: Context, view:View) {
            view.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        fun hideKeyBoard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}