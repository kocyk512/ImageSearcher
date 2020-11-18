package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.clicks
import com.krzysztofkocot.imagesearcher.R
import com.krzysztofkocot.imagesearcher.data.bluetooth.BluetoothDeviceDomain
import com.krzysztofkocot.imagesearcher.databinding.ItemBluetoothDeviceBinding

class BluetoothDevicesAdapter() :
    ListAdapter<BluetoothDeviceDomain, BluetoothDevicesAdapter.BluetoothDeviceViewHolder>(BLUETOOTH_DIFF_UTIL) {

    private var listener: OnItemClickListener? = null

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

    interface OnItemClickListener {
        fun onItemClick(device: BluetoothDeviceDomain)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class BluetoothDeviceViewHolder(private val binding: ItemBluetoothDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: BluetoothDeviceDomain) {
            with(binding) {
                textViewBluetoothDevice.apply {
                    text = device.description
                    clicks().subscribe { listener?.onItemClick(device) }
                }
                if (device.isPaired) {
                    imageViewChecked.isVisible = true
                    imageViewChecked.setImageResource(R.drawable.ic_check)
                    textViewPaired.isVisible = true
                } else {
                    imageViewChecked.isVisible = false
                    textViewPaired.isVisible = false
                }
            }
        }
    }

    companion object {
        private val BLUETOOTH_DIFF_UTIL = object : DiffUtil.ItemCallback<BluetoothDeviceDomain>() {
            override fun areItemsTheSame(oldItem: BluetoothDeviceDomain, newItem: BluetoothDeviceDomain) =
                oldItem.address == newItem.address

            override fun areContentsTheSame(oldItem: BluetoothDeviceDomain, newItem: BluetoothDeviceDomain) =
                oldItem == newItem
        }
    }
}