package com.example.imagesearcher.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.imagesearcher.PixbayViewModel
import com.example.imagesearcher.R
import com.example.imagesearcher.databinding.FragmentGalleryBinding
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel by viewModels<PixbayViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private var disposable = CompositeDisposable()

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

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        disposable.add(
            searchView.queryTextChanges()
                .skipInitialValue()
                .debounce(700L, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .subscribeAndroid {
                    if (it.isNotBlank()) {
                        binding.recyclerView.scrollToPosition(0)
                        viewModel.searchPhotos(it.toString())
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        disposable.clear()
    }
}