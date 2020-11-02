package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.bluetooth.BluetoothDeviceDomain
import com.krzysztofkocot.imagesearcher.databinding.FragmentBluetoothDevicesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class BluetoothDevicesFragment : Fragment(R.layout.fragment_bluetooth_devices) {

    private var _binding: FragmentBluetoothDevicesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var bluetoothDevicesAdapter: BluetoothDevicesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentBluetoothDevicesBinding.bind(view)

        val devices = listOf(device(), device(), device(), device(), device())
        bluetoothDevicesAdapter.submitList(devices)

        binding.apply {
            recyclerViewFavourites.apply {
                adapter = bluetoothDevicesAdapter
                setHasFixedSize(true)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

fun device() = BluetoothDeviceDomain("addres", "name", "alias", "type", Random.nextInt(0, 1000).toString())
