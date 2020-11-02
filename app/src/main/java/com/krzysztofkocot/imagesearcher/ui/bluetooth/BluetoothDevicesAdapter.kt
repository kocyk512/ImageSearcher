package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krzysztofkocot.imagesearcher.data.bluetooth.BluetoothDeviceDomain
import com.krzysztofkocot.imagesearcher.databinding.ItemBluetoothDeviceBinding
import kotlinx.android.synthetic.main.item_bluetooth_device.view.text_view_bluetooth_device

class BluetoothDevicesAdapter() :
    ListAdapter<BluetoothDeviceDomain, BluetoothDevicesAdapter.BluetoothDeviceViewHolder>(BLUETOOTH_DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        val binding =
            ItemBluetoothDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BluetoothDeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class BluetoothDeviceViewHolder(private val binding: ItemBluetoothDeviceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(device: BluetoothDeviceDomain) {
            with (binding.root) {
                text_view_bluetooth_device.text = device.toString()
            }
        }
    }

    companion object {
        private val BLUETOOTH_DIFF_UTIL = object : DiffUtil.ItemCallback<BluetoothDeviceDomain>() {
            override fun areItemsTheSame(oldItem: BluetoothDeviceDomain, newItem: BluetoothDeviceDomain) =
                oldItem.uuids == newItem.uuids

            override fun areContentsTheSame(oldItem: BluetoothDeviceDomain, newItem: BluetoothDeviceDomain) =
                oldItem == newItem
        }
    }
}