package com.example.imagesearcher.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.imagesearcher.PixbayViewModel
import com.example.imagesearcher.R
import com.example.imagesearcher.data.remote.PixbayPhoto
import com.example.imagesearcher.databinding.FragmentGalleryBinding
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import com.jakewharton.rxbinding4.view.clicks
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), PixbayPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<PixbayViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private var disposable = CompositeDisposable()

    @Inject
    lateinit var pixbayPhotoAdapter: PixbayPhotoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        setupView()

        setupViewModel()

        setHasOptionsMenu(true)
    }

    private fun setupView() {
        binding.apply {
            with(recyclerView) {
                adapter = pixbayPhotoAdapter.withLoadStateHeaderAndFooter(
                    header = PixbayLoadStateAdapter(viewModel.footerHeaderRefreshClickS),
                    footer = PixbayLoadStateAdapter(viewModel.footerHeaderRefreshClickS)
                )
                setHasFixedSize(true)
                itemAnimator = null
            }
            buttonRetry.clicks()
                .subscribe { viewModel.retryClickS.postValue(Unit) }
        }
        pixbayPhotoAdapter.setOnItemClickListener(this)
        pixbayPhotoAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    pixbayPhotoAdapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    private fun setupViewModel() = viewModel.apply {
        photos.subscribe(viewLifecycleOwner) {
            pixbayPhotoAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        footerHeaderRefreshClickS.subscribe(viewLifecycleOwner) {
            pixbayPhotoAdapter.retry()
        }
        retryClickS.subscribe(viewLifecycleOwner) {
            pixbayPhotoAdapter.retry()
        }
    }

    override fun onItemClick(photo: PixbayPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onFavouriteClick(photo: PixbayPhoto) = viewModel.insertPhoto(photo)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_favourites -> {
            val action = GalleryFragmentDirections.actionGalleryFragmentToFavouritesFragment()
            findNavController().navigate(action)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        disposable.clear()
    }
}