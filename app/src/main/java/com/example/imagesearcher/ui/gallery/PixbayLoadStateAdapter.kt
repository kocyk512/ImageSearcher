package com.example.imagesearcher.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesearcher.databinding.LoadStateFooterHeaderBinding
import com.jakewharton.rxbinding4.view.clicks

class PixbayLoadStateAdapter(val footerHeaderRefreshClickS: MutableLiveData<Unit>) :
    LoadStateAdapter<PixbayLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LoadStateFooterHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    inner class LoadStateViewHolder(private val binding: LoadStateFooterHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.clicks().subscribe {
                footerHeaderRefreshClickS.postValue(Unit)
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}
