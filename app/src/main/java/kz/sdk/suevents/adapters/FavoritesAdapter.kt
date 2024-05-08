package kz.sdk.suevents.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import kz.sdk.suevents.R
import kz.sdk.suevents.base.BaseEventViewHolder
import kz.sdk.suevents.databinding.ItemFavoritesBinding
import kz.sdk.suevents.models.Event

class FavoritesAdapter:ListAdapter<Event, BaseEventViewHolder<*>>(EventDiffUtils()){

    var itemClick:((Event) -> Unit)? = null

    var deleteButtonClicked: ((Event)->Unit)? = null

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
            ItemFavoritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseEventViewHolder<*>, position: Int) {
        holder.bindView(getItem(position))
    }
    inner class EventViewHolder(binding:ItemFavoritesBinding):BaseEventViewHolder<ItemFavoritesBinding>(binding){
        override fun bindView(item: Event) {
            with(binding){
                title.text = item.title
                date.text = item.date
                time.text = item.time
                Glide.with(itemView.context)
                    .load(item.img)
                    .placeholder(R.drawable.placeholder_event)
                    .into(imageView)
                deleteBtn.setOnClickListener {
                    deleteButtonClicked?.invoke(item)
                }
            }
            itemView.setOnClickListener {
                itemClick?.invoke(item)
            }
        }

    }
}