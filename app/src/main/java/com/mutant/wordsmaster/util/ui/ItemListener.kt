package com.mutant.wordsmaster.util.ui

interface ItemListener<T> {

    fun onItemClick(data: T)

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemSwipe(position: Int)

}