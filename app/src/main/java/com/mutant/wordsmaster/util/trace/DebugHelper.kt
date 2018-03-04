package com.mutant.wordsmaster.util.trace

import android.os.Environment
import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

class DebugHelper {

    companion object {
        private var mIsDebugEnabled: Boolean = false
        private var mDebugLogPath: String = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
        private var mDataOutputStream: DataOutputStream? = null
        private val TAG = DebugHelper::class.java.simpleName

        fun setDebugEnabled(enabled: Boolean) {
            mIsDebugEnabled = enabled
        }

        fun setDebugLogPath(path: String) {
            mDebugLogPath = path
        }

        fun i(tag: String, msg: Any) {
            if (mIsDebugEnabled) {
                Log.i(tag, "" + msg)
                writeLog(tag, "" + msg)
            }
        }

        fun e(tag: String, msg: Any) {
            if (mIsDebugEnabled) {
                Log.e(tag, getClassLineNumber(tag) + msg)
                writeLog(tag, "" + msg)
            }
        }

        fun w(tag: String, msg: Any) {
            if (mIsDebugEnabled) {
                Log.w(tag, "" + msg)
                writeLog(tag, "" + msg)
            }
        }

        fun wtf(tag: String, msg: Any) {
            if (mIsDebugEnabled) {
                Log.wtf(tag, "" + msg)
                writeLog(tag, "" + msg)
            }
        }

        private fun writeLog(tag: String, msg: String) {
            if (mDebugLogPath == null) {
                return
            }
            try {
                if (mDataOutputStream == null) {
                    val calendar = Calendar.getInstance()
                    val dateTime = String.format("%04d%02d%02d_%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                    val logTimeFolder = File(mDebugLogPath, dateTime)
                    if (!logTimeFolder.exists()) {
                        if (!logTimeFolder.mkdir()) {
                            return
                        }
                    }
                    val logFile = File(logTimeFolder.absolutePath, "debugLog.log")
                    if (!logFile.exists()) {
                        logFile.createNewFile()
                    }
                    mDataOutputStream = DataOutputStream(FileOutputStream(logFile))
                }
                mDataOutputStream!!.write(String.format("[%s]\t%s\n", DateFormat.getDateTimeInstance().format(Date()), msg).toByteArray())
            } catch (e: Exception) {
                Log.e(tag, Log.getStackTraceString(e))
            }

        }

        private fun getClassLineNumber(tag: String): String {
            try {
                val stackTraceElements = Thread.currentThread().stackTrace
                if (stackTraceElements == null || stackTraceElements.isEmpty()) {
                    return ""
                }
                var deep = 0
                for (i in stackTraceElements.indices) {
                    val className = stackTraceElements[i].className
                    if (className.indexOf(DebugHelper::class.java.simpleName) >= 0) {
                        deep++
                    }
                    if (deep == 2) {
                        return ("[" + stackTraceElements[i + 1].fileName + " line:"
                                + stackTraceElements[i + 1].lineNumber + "] ")
                    }
                }
                return ""
            } catch (e: Exception) {
                Log.i(tag, Log.getStackTraceString(e))
                return ""
            }
        }
    }
}
