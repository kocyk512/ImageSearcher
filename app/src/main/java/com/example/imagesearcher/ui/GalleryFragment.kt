package com.example.imagesearcher.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.imagesearcher.PixbayViewModel
import com.example.imagesearcher.R
import com.example.imagesearcher.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel by viewModels<PixbayViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        val adapter = PixbayPhotoAdapter()

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PixbayLoadStateAdapter(viewModel.footerHeaderRefreshClickS),
                footer = PixbayLoadStateAdapter(viewModel.footerHeaderRefreshClickS)
            )
        }

        viewModel.photos
            .subscribe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

        viewModel.footerHeaderRefreshClickS
            .subscribe(viewLifecycleOwner) {
                adapter.retry()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}