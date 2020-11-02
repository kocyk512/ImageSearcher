package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.krzysztofkocot.imagesearcher.ENABLE_BLUETOOTH_REQUEST_CODE
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.databinding.FragmentBluetoothDevicesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothDevicesFragment : Fragment(R.layout.fragment_bluetooth_devices) {

    private var _binding: FragmentBluetoothDevicesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var recyclerAdapter: BluetoothDevicesAdapter

    private var _bluetoothAdapter: BluetoothAdapter? = null

    private lateinit var _bluetoothOnOffReceiver: BluetoothOnOffReceiver

    private val _bluetoothDiscoverReceiver = BluetoothDiscoverReceiver()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBluetooth()

        setupView(view)

        setupReceivers()
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
                    discover(_bluetoothAdapter!!)
                }
            progressBar.isVisible = true
        }
    }

    private fun setupBluetooth() {
        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkBTPermissions(requireActivity())

        _bluetoothAdapter.let {
            if (it == null) {
                binding.apply {
                    progressBar.isVisible = false
                    textViewNoBluetooth.isVisible = true
                }
            } else {
                if (!it.isEnabled) {
                    Log.d("KK", "enableDisableBT: enabling BT.")
                    val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
                } else {
                    recyclerAdapter.submitList(null)
                    discover(_bluetoothAdapter!!)
                }
            }
        }
    }

    private fun setupReceivers() {
        _bluetoothOnOffReceiver = BluetoothOnOffReceiver()
        val onOffFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        activity?.registerReceiver(_bluetoothOnOffReceiver, onOffFilter)

        val discoverDevicesFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        activity?.registerReceiver(_bluetoothDiscoverReceiver, discoverDevicesFilter)
        _bluetoothOnOffReceiver.onStateOn = {
            recyclerAdapter.submitList(null)
            discover(_bluetoothAdapter!!)
        }

        _bluetoothDiscoverReceiver.devicesS
            .doOnError {
                Log.d("KK", "error", it)
            }
            .subscribe {
                Log.d("KK", "subscribed - $it")
                recyclerAdapter.submitList(it.distinct())
                binding.apply {
                    progressBar.isVisible = false
                    swipeContainer.isRefreshing = false
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bluetoothAdapter?.disable()
        activity?.apply {
            unregisterReceiver(_bluetoothDiscoverReceiver)
            unregisterReceiver(_bluetoothOnOffReceiver)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ENABLE_BLUETOOTH_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> { }
                Activity.RESULT_CANCELED -> {
                    recyclerAdapter.submitList(null)
                    binding.apply {
                        progressBar.isVisible = false
                        textViewNoBluetooth.isVisible = true
                    }
                }
            }
        }
    }
}
