package kz.sdk.suevents.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseEventViewHolder
import kz.sdk.suevents.databinding.ItemEventBinding
import kz.sdk.suevents.models.Event

class EventAdapter:ListAdapter<Event, BaseEventViewHolder<*>>(EventDiffUtils()) {


    var itemClick:((Event)->Unit)? = null
    class EventDiffUtils:DiffUtil.ItemCallback<Event>(){
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseEventViewHolder<*> {
        return EventViewHolder(
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseEventViewHolder<*>, position: Int) {
        holder.bindView(getItem(position))
    }
    inner class EventViewHolder(binding:ItemEventBinding):BaseEventViewHolder<ItemEventBinding>(binding){
        override fun bindView(item: Event) {
            with(binding){
                title.text = item.title
                date.text = item.date
                time.text = item.time
                Glide.with(itemView.context)
                    .load(item.img)
                    .placeholder(R.drawable.placeholder_event)
                    .into(imageView)
            }
            itemView.setOnClickListener {
                itemClick?.invoke(item)
            }
        }

    }
}