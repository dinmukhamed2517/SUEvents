package kz.sdk.suevents.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kz.sdk.suevents.models.Event
import kz.sdk.suevents.models.Filter

abstract class BaseViewHolder<VB : ViewBinding, T>(protected open val binding: VB) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bindView(item: T)
}

abstract class BaseEventViewHolder<VB : ViewBinding>(override val binding: VB) :
    BaseViewHolder<VB, Event>(binding)


abstract class BaseFilterViewHolder<VB : ViewBinding>(override val binding: VB) :
    BaseViewHolder<VB, Filter>(binding)