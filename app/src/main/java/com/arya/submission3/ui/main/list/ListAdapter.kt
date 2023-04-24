package com.arya.submission3.ui.main.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arya.submission3.R
import com.arya.submission3.data.remote.response.Story
import com.arya.submission3.databinding.ItemListBinding
import com.arya.submission3.utils.withDateFormat
import com.bumptech.glide.Glide

class ListAdapter(private val callback: (Story, ImageView, TextView) -> Unit) : PagingDataAdapter<Story, ListAdapter.ListViewHolder>(DIFF_CALLBACK) {
    inner class ListViewHolder(val binding: ItemListBinding) :  ViewHolder(binding.root) {
        fun setData(story: Story) {
            Glide.with(itemView).load(story.photoUrl).into(binding.imageItem)
            binding.nameItem.text = story.name
            binding.dateItem.text = itemView.context.getString(R.string.dateFormat,
                story.createdAt?.withDateFormat() ?: story.createdAt
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.setData(data)

            holder.binding.imageItem.transitionName = "image_${data.id}"
            holder.binding.nameItem.transitionName = "name_${data.id}"
            holder.itemView.setOnClickListener { callback(data, holder.binding.imageItem, holder.binding.nameItem) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}