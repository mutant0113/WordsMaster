/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mutant.wordsmaster.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * This provides methods to help Activities load their UI.
 */
object ActivityUtils {

    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     *
     */
    fun addFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int, tag: String) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment, tag)
        transaction.commit()
        fragmentManager.executePendingTransactions()
    }

    fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment, frameId: Int, tag: String) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment, tag)
        transaction.commit()
    }

    fun hideFragment(fragmentManager: FragmentManager, tag: String?) {
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.hide(fragment)
            transaction.commit()
        }
    }

    fun showFragment(fragmentManager: FragmentManager, tag: String) {
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.show(fragment)
            transaction.commit()
        }
    }

    /**
     * Bring fragment with tag to the top and hide all other fragments
     */
    fun switchFragment(fragmentManager: FragmentManager, tag: String) {
        val fragmentShow = fragmentManager.findFragmentByTag(tag)
        if(fragmentShow != null) {
            for(fragment in fragmentManager.fragments) {
                hideFragment(fragmentManager, fragment.tag)
            }
            showFragment(fragmentManager, tag)
        }
    }

}
