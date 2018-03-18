package com.mutant.wordsmaster.util.ui

import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemTouchHelperCallback<T>(private val mItemListener: ItemListener<T>) :
        ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return mItemListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mItemListener.onItemSwipe(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.7f
            viewHolder?.itemView?.setBackgroundColor(Color.YELLOW)
        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        clearHighlight(viewHolder)
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width: Int? = viewHolder?.itemView?.width
            val alpha = if (width != null) 1 - Math.abs(dX) / width else 1f

            viewHolder?.itemView?.alpha = alpha
            // Set background color to white when dx is 0 which means not to swipe
            if (dX == 0f) viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
            else viewHolder?.itemView?.setBackgroundColor(Color.YELLOW)
        } else if (actionState == ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL) {
            clearHighlight(viewHolder)
        }
    }

    fun clearHighlight(viewHolder: RecyclerView.ViewHolder?) {
        viewHolder?.itemView?.alpha = 1.0f
        viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
    }
}