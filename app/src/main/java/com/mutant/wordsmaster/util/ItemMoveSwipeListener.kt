package com.mutant.wordsmaster.util

interface ItemMoveSwipeListener {

    fun onItemMove(fromPostion: Int, toPosition: Int) : Boolean

    fun onItemSwipe(position: Int)
}