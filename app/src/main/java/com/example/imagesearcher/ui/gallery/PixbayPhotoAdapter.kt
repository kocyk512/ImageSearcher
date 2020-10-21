package com.example.imagesearcher.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.imagesearcher.ANIMATION_DURATION
import com.example.imagesearcher.R
import com.example.imagesearcher.data.remote.PixbayPhoto
import com.example.imagesearcher.databinding.ItemPixbayPhotoBinding
import com.example.imagesearcher.ui.favourites.jumpAnimate
import kotlinx.android.synthetic.main.fragment_details.view.image_view_main
import kotlinx.android.synthetic.main.item_pixbay_photo.view.floating_button
import kotlinx.android.synthetic.main.item_pixbay_photo.view.image_view_main_item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixbayPhotoAdapter @Inject constructor() :
    PagingDataAdapter<PixbayPhoto, PixbayPhotoAdapter.PixbayViewHolder>(
        PHOTO_DIFF_UTIL
    ) {

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
        fun onFavouriteClick(photo: PixbayPhoto)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setFavouriteIds(ids: List<Int?>) {
        favouritePhotosIds.clear()
        favouritePhotosIds.addAll(ids)
        GlobalScope.launch(Dispatchers.Main) {
            delay(ANIMATION_DURATION)
            notifyDataSetChanged()
        }
    }

    private val favouritePhotosIds = mutableListOf<Int?>()

    inner class PixbayViewHolder(private val binding: ItemPixbayPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding.root) {
                image_view_main_item.setOnClickListener {
                    getCurrentItem()?.let { listener?.onItemClick(it) }
                }
                floating_button.setOnClickListener { view ->
                    getCurrentItem()?.let {
                        listener?.onFavouriteClick(it)
                    }
                    view.jumpAnimate()
                }
            }
        }

        private fun getCurrentItem(): PixbayPhoto? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) getItem(position) else null
        }

        fun bind(pixbayPhoto: PixbayPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(pixbayPhoto.webformatURL)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageViewMainItem)

                Glide.with(itemView)
                    .load(pixbayPhoto.userImageURL)
                    .circleCrop()
                    .error(R.drawable.ic_user)
                    .into(UserGlideTarget(textViewUserName))

                textViewUserName.text = pixbayPhoto.user
                floatingButton.setImageResource(
                    if (favouritePhotosIds.contains(pixbayPhoto.id)) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                )
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
