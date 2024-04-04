package com.example.cmed_task.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cmed_task.base.BaseViewHolder

internal fun RecyclerView.grid(
    adapt: RecyclerView.Adapter<BaseViewHolder>
) {
    with(this) {
        layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        itemAnimator = DefaultItemAnimator()
        isNestedScrollingEnabled = true
        adapter = adapt
    }
}

internal fun RecyclerView.with(adapt: RecyclerView.Adapter<BaseViewHolder>) {
    with(this) {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        itemAnimator = DefaultItemAnimator()
        isNestedScrollingEnabled = true
        adapter = adapt
    }
}

internal fun RecyclerView.overScrollNever() {
    overScrollMode = View.OVER_SCROLL_NEVER
}

internal fun RecyclerView.nestedDisable() {
    ViewCompat.setNestedScrollingEnabled(this, false)
}