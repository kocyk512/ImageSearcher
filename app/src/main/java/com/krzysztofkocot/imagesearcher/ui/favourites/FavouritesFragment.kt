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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkBTPermissions()
        val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity?.registerReceiver(mBroadcastReceiver3, discoverDevicesIntent)

    }

    override fun onAddFavouriteClick(photo: PixbayDBItem, drawable: Drawable) {
        val permissionGranted = checkPermissionWRITE_EXTERNAL_STORAGE(requireContext(), activity as Activity)
        if (permissionGranted) {
            savebyMediaStore(photo, drawable, requireContext())
            toast(requireContext(), getString(R.string.photo_will_be))
        } else toast(requireContext(), getString(R.string.try_toSave_again))
    }

    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onBluetoothClick() {
        val action = FavouritesFragmentDirections.actionFavouritesFragmentToBluetoothDevicesFragment()
        findNavController().navigate(action)

/*
        if (mBluetoothAdapter == null) {
            Log.d("KK", "enableDisableBT: Does not have BT capabilities.")
            return
        }
        if (!mBluetoothAdapter.isEnabled) {
            Log.d("KK", "enableDisableBT: enabling BT.")
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ContextCompat.startActivity(requireContext(), enableBTIntent, null)
            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            activity?.registerReceiver(mBroadcastReceiver1, intentFilter)
        }
        if (mBluetoothAdapter.isEnabled) {
            Log.d("KK", "enableDisableBT: disabling BT.")
            mBluetoothAdapter.disable()
            val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            activity?.registerReceiver(mBroadcastReceiver1, intentFilter)
        }*/
    }

    private val mBroadcastReceiver1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.d("KK", "onReceive: STATE OFF")
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                        "KK",
                        "mBroadcastReceiver1: STATE TURNING OFF"
                    )
                    BluetoothAdapter.STATE_ON -> discover()
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                        "KK",
                        "mBroadcastReceiver1: STATE TURNING ON"
                    )
                }
            }
        }
    }

    fun discover() {
        Log.d("KK", "discover(): Looking for unpaired devices.")
        mBluetoothAdapter?.let{
            if (mBluetoothAdapter!!.isDiscovering) {
                mBluetoothAdapter!!.cancelDiscovery()
                Log.d("KK", "discover(): Canceling discovery.")

                //check BT permissions in manifest
//            checkBTPermissions()
                mBluetoothAdapter!!.startDiscovery()
            }
            if (!mBluetoothAdapter!!.isDiscovering) {
                Log.d("KK", "discover(): Discovering.")

                //check BT permissions in manifest
//            checkBTPermissions()
                mBluetoothAdapter!!.startDiscovery()

            }
        }
    }

    private fun checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (activity != null) {
                var permissionCheck =
                    (activity as FragmentActivity).checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
                permissionCheck += (activity as FragmentActivity).checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
                if (permissionCheck != 0) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ), 1001
                    )
                }
            }
        } else {
            Log.d("KK", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.")
        }
    }

    private val mBroadcastReceiver3: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            Log.d("KK", "onReceive: ACTION FOUND.")

            if (action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                mBTDevices.add(device)
                val description = buildString {
                    append(device!!.address)
                    appendLine()
                    append(device!!.name)
                    appendLine()
                    append(device!!.alias)
                    appendLine()
                    append(device!!.type)
                    appendLine()
                    append(device!!.uuids)
                    appendLine()
                }
                Log.d("KK", "onReceive: $description")
//                Log.d("KK", "onReceive: " + device!!.name + ": " + device!!.address)
//                mDeviceListAdapter = DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices)
//                lvNewDevices.setAdapter(mDeviceListAdapter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}