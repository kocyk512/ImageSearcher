package com.krzysztofkocot.imagesearcher.ui.favourites

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.databinding.ItemPixbayPhotoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.item_pixbay_photo.view.floating_button
import kotlinx.android.synthetic.main.item_pixbay_photo.view.image_view_main_item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesAdapter @Inject constructor() :
    ListAdapter<PixbayDBItem, FavouritesAdapter.PixbayViewHolder>(PHOTO_DIFF_UTIL) {

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
        fun onFloatingBtnClick(photo: PixbayDBItem, drawable: Drawable)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class PixbayViewHolder(private val binding: ItemPixbayPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding.root) {
                floating_button.setOnClickListener { view ->
                    getCurrentItem()?.let {
                        if (view is FloatingActionButton) {
                            val drawable = image_view_main_item.drawable
                            listener?.onFloatingBtnClick(it, drawable)
                        }
                    }
                    view.jumpAnimate()
                }
            }
        }

        private fun getCurrentItem(): PixbayDBItem? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) getItem(position) else null
        }

        fun bind(pixbayPhoto: PixbayDBItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(pixbayPhoto.webFormatUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageViewMainItem)

                textViewUserName.text = pixbayPhoto.user
                floatingButton.setImageResource(R.drawable.ic_save)
            }
        }
    }

    companion object {
        private val PHOTO_DIFF_UTIL = object : DiffUtil.ItemCallback<PixbayDBItem>() {
            override fun areItemsTheSame(oldItem: PixbayDBItem, newItem: PixbayDBItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PixbayDBItem, newItem: PixbayDBItem) =
                oldItem == newItem
        }
    }
}
