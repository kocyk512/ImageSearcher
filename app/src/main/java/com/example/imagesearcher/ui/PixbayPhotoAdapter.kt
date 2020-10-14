package com.example.imagesearcher.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.imagesearcher.R
import com.example.imagesearcher.data.PixbayPhoto
import com.example.imagesearcher.databinding.ItemPixbayPhotoBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixbayPhotoAdapter @Inject constructor() :
    PagingDataAdapter<PixbayPhoto, PixbayPhotoAdapter.PixbayViewHolder>(PHOTO_DIFF_UTIL) {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixbayViewHolder {
        val binding =
            ItemPixbayPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PixbayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PixbayViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: PixbayPhoto)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class PixbayViewHolder(private val binding: ItemPixbayPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener?.onItemClick(item)
                    }
                }
            }
        }

        fun bind(pixbayPhoto: PixbayPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(pixbayPhoto.webformatURL)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageViewMain)

                textViewUserName.text = pixbayPhoto.user
            }
        }
    }

    companion object {
        private val PHOTO_DIFF_UTIL = object : DiffUtil.ItemCallback<PixbayPhoto>() {
            override fun areItemsTheSame(oldItem: PixbayPhoto, newItem: PixbayPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PixbayPhoto, newItem: PixbayPhoto) =
                oldItem == newItem
        }
    }
}
