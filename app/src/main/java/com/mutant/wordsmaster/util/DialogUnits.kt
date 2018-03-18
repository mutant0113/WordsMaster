package com.mutant.wordsmaster.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.mutant.wordsmaster.R

class DialogUnits {

    companion object {

        fun createDialog(context: Context, title: String, msg: String,
                         onNegativeClickListener: DialogInterface.OnClickListener,
                         onPositiveClickListener: DialogInterface.OnClickListener): Dialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                    .setMessage(msg)
                    .setNegativeButton(context.getString(R.string.cancel), onNegativeClickListener)
                    .setPositiveButton(context.getString(R.string.confirm), onPositiveClickListener)

            return builder.create()
        }

    }
}