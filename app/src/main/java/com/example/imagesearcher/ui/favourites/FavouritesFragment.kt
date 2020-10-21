package com.example.imagesearcher.ui.favourites

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.imagesearcher.R
import com.example.imagesearcher.data.local.PixbayDBItem
import com.example.imagesearcher.databinding.FragmentFavouritesBinding
import com.example.imagesearcher.subscribe
import com.example.imagesearcher.ui.utils.checkPermissionWRITE_EXTERNAL_STORAGE
import com.example.imagesearcher.ui.utils.savebyMediaStore
import com.example.imagesearcher.ui.utils.toast
import com.example.imagesearcher.viewmodel.PixbayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourites), FavouritesAdapter.OnItemClickListener {

    private val viewModel by viewModels<PixbayViewModel>()

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var favouritesAdapter: FavouritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouritesBinding.bind(view)

        binding.apply {
            recyclerViewFavourites.apply {
                adapter = favouritesAdapter
                setHasFixedSize(true)
            }
        }

        viewModel.apply {
            allFavouritesPhoto().subscribe(viewLifecycleOwner) { items ->
                viewModelScope.launch {
                    favouritesAdapter.submitList(items)
                }
            }
        }

        favouritesAdapter.setOnItemClickListener(this)
    }

    override fun onFloatingBtnClick(photo: PixbayDBItem, drawable: Drawable) {
        val permissionGranted = checkPermissionWRITE_EXTERNAL_STORAGE(requireContext(), activity as Activity)
        if (permissionGranted) {
            savebyMediaStore(photo, drawable, requireContext())
            toast(requireContext(), getString(R.string.photo_will_be))
        } else toast(requireContext(), getString(R.string.try_toSave_again))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}