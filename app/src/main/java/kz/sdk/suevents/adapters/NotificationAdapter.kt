package kz.sdk.suevents.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kz.sdk.suevents.base.BaseEventViewHolder
import kz.sdk.suevents.databinding.ItemFavoritesBinding
import kz.sdk.suevents.databinding.ItemNotificationBinding
import kz.sdk.suevents.models.Event

class NotificationAdapter:ListAdapter<Event, BaseEventViewHolder<*>>(EventDiffUtils()) {
    class EventDiffUtils: DiffUtil.ItemCallback<Event>(){
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseEventViewHolder<*> {
        return EventViewHolder(
           ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseEventViewHolder<*>, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class EventViewHolder(binding:ItemNotificationBinding):BaseEventViewHolder<ItemNotificationBinding>(binding){
        override fun bindView(item: Event) {
            with(binding){
                title.text = item.title
                time.text = item.time
            }
        }

    }
}