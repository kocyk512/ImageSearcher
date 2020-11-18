package com.krzysztofkocot.imagesearcher.ui.details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding4.view.clicks
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.remote.toDescription
import com.krzysztofkocot.imagesearcher.databinding.FragmentDetailsBinding
import com.krzysztofkocot.imagesearcher.ui.gallery.GalleryFragmentDirections
import kotlinx.android.synthetic.main.fragment_details.floating_button

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)
        binding.apply {

            val photo = args.photo

            Glide.with(this@DetailsFragment)
                .load(photo.webformatURL)
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewUser.isVisible = true
                        textViewTags.isVisible = photo.tags.isNotBlank()
                        return false
                    }
                })
                .into(imageViewMain)

            Glide.with(this@DetailsFragment)
                .load(photo.userImageURL)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewUserPhoto)

            textViewTags.text = photo.tags
            textViewUser.text = photo.user
            textViewDescription.text = photo.toDescription()
            floatingButton.clicks()
                .subscribe {
                    val action =
                        DetailsFragmentDirections.actionDetailsFragmentToBluetoothDevicesFragment()
                    findNavController().navigate(action)
                }
        }
    }
}