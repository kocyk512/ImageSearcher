package com.krzysztofkocot.imagesearcher.ui.favourites

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.databinding.FragmentFavouritesBinding
import com.krzysztofkocot.imagesearcher.di.AppModule
import com.krzysztofkocot.imagesearcher.subscribe
import com.krzysztofkocot.imagesearcher.ui.utils.checkPermissionWRITE_EXTERNAL_STORAGE
import com.krzysztofkocot.imagesearcher.ui.utils.savebyMediaStore
import com.krzysztofkocot.imagesearcher.ui.utils.toast
import com.krzysztofkocot.imagesearcher.viewmodel.PixbayViewModel
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

    override fun onAddFavouriteClick(photo: PixbayDBItem, drawable: Drawable) {
        val permissionGranted = checkPermissionWRITE_EXTERNAL_STORAGE(requireContext(), activity as Activity)
        if (permissionGranted) {
            savebyMediaStore(photo, drawable, requireContext())
            toast(requireContext(), getString(R.string.photo_will_be))
        } else toast(requireContext(), getString(R.string.try_toSave_again))
    }

    override fun onBluetoothClick() {
        val action = FavouritesFragmentDirections.actionFavouritesFragmentToBluetoothDevicesFragment()
        findNavController().navigate(action)

    }





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}