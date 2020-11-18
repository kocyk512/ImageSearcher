package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.krzysztofkocot.imagesearcher.ENABLE_BLUETOOTH_REQUEST_CODE
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.bluetooth.BluetoothDeviceDomain
import com.krzysztofkocot.imagesearcher.databinding.FragmentBluetoothDevicesBinding
import com.krzysztofkocot.imagesearcher.subscribe
import com.krzysztofkocot.imagesearcher.ui.utils.log
import com.krzysztofkocot.imagesearcher.viewmodel.PixbayViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothDevicesFragment : Fragment(R.layout.fragment_bluetooth_devices),
    BluetoothDevicesAdapter.OnItemClickListener {

    private val viewModel by viewModels<PixbayViewModel>()

    private var _binding: FragmentBluetoothDevicesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var recyclerAdapter: BluetoothDevicesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBluetooth()

        setupView(view)

        observeViewModel()
    }

    private fun setupView(view: View) {
        _binding = FragmentBluetoothDevicesBinding.bind(view)

        binding.apply {
            recyclerViewDevices.apply {
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }
            swipeContainer.refreshes()
                .subscribe {
                    recyclerAdapter.apply {
                        submitList(null)
                        notifyDataSetChanged()
                    }
                    viewModel.refresh()
                }
            progressBar.isVisible = true
        }

        recyclerAdapter.setOnItemClickListener(this)
    }

    private fun setupBluetooth() = viewModel.bluetoothAdapter.subscribe(viewLifecycleOwner) {
        if (it == null) {
            binding.apply {
                progressBar.isVisible = false
                textViewNoBluetooth.isVisible = true
            }
        } else {
            checkBTPermissions(requireActivity())
            if (!it.isEnabled) {
                Log.d("KK", "enableDisableBT: enabling BT.")
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
            } else {
                recyclerAdapter.apply {
                    submitList(null)
                    recyclerAdapter.notifyDataSetChanged()
                }
                discover(it)
            }
        }
    }

    private fun observeViewModel() = viewModel.apply {
        setupReceivers()
        broadcastReceivers.subscribe(viewLifecycleOwner) { receivers ->
            receivers.forEach {
                activity?.registerReceiver(it.key, it.value)
            }
        }
        bluetoothOn.subscribe(viewLifecycleOwner) {
            recyclerAdapter.apply {
                submitList(null)
                notifyDataSetChanged()
            }
        }
        devices.subscribe(viewLifecycleOwner) {
            recyclerAdapter.apply {
                submitList(it)
                notifyDataSetChanged()
            }
            binding.apply {
                progressBar.isVisible = false
                swipeContainer.isRefreshing = false
            }
        }
        deviceBonding.subscribe(viewLifecycleOwner) {
            binding.progressBarBonding.isVisible = it
        }
    }


    override fun onItemClick(device: BluetoothDeviceDomain) {
        log("create bond")
        viewModel.bondDevice(device.device)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        recyclerAdapter.submitList(null)
        _binding = null
        viewModel.broadcastReceivers.value?.forEach {
            activity?.unregisterReceiver(it.key)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ENABLE_BLUETOOTH_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                }
                Activity.RESULT_CANCELED -> {
                    recyclerAdapter.apply {
                        submitList(null)
                        notifyDataSetChanged()
                    }
                    binding.apply {
                        progressBar.isVisible = false
                        textViewNoBluetooth.isVisible = true
                    }
                }
            }
        }
    }
}
