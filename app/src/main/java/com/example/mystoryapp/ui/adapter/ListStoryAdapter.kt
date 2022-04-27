package com.example.mystoryapp.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystoryapp.ListStoryItem
import com.example.mystoryapp.databinding.ItemRowStoryBinding
import com.example.mystoryapp.ui.detail.DetailStoryActivity


class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(
    DIFF_CALLBACK) {
    private val listItem = ArrayList<ListStoryItem>()

//    fun setListStory(items: List<ListStoryItem>) {
//        val diffStoryDiffCallback = StoryDiffCallback(this.listItem, items)
//        val diffStoryResult = DiffUtil.calculateDiff(diffStoryDiffCallback)
//        listItem.clear()
//        listItem.addAll(items)
//        diffStoryResult.dispatchUpdatesTo(this)
//    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListStoryAdapter.ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryAdapter.ListViewHolder, position: Int) {

        val listData = getItem(position)
        if (listData != null){
            holder.bind(listData)
        }
    }

//    override fun getItemCount(): Int = listItem.size

    inner class ListViewHolder(private var binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .into(imgStories)
                tvName.text = item.name
                tvCreated.text = item.createdAt
                tvDescription.text = item.description

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.EXTRA_DETAIL, item)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgStories, "image"),
                            Pair(tvName, "name"),
                            Pair(tvCreated, "created"),
                            Pair(tvDescription, "description"),
                        )

                    itemView.context.startActivity(intent, optionsCompat.toBundle())

                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}


