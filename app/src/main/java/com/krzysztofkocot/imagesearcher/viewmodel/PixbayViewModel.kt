package com.krzysztofkocot.imagesearcher.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.krzysztofkocot.imagesearcher.data.RepositoryContract
import com.krzysztofkocot.imagesearcher.data.bluetooth.BluetoothDeviceDomain
import com.krzysztofkocot.imagesearcher.data.remote.PixbayPhoto
import com.krzysztofkocot.imagesearcher.data.remote.toDbItem
import com.krzysztofkocot.imagesearcher.ui.bluetooth.BluetoothBondReceiver
import com.krzysztofkocot.imagesearcher.ui.bluetooth.BluetoothDiscoverReceiver
import com.krzysztofkocot.imagesearcher.ui.bluetooth.BluetoothOnOffReceiver
import com.krzysztofkocot.imagesearcher.ui.bluetooth.discover
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class PixbayViewModel @ViewModelInject constructor(
    private val repository: RepositoryContract,
    @Assisted state: SavedStateHandle,
) : ViewModel() {

    private val disposable = CompositeDisposable()
    val footerHeaderRefreshClickS = MutableLiveData<Unit>()
    val retryClickS = MutableLiveData<Unit>()
    private val _queryStatus = MutableLiveData<QueryEvent>()
    val queryStatus: LiveData<QueryEvent> = _queryStatus

    private val currentQuery = state.getLiveData(
        CURRENT_QUERY,
        DEFAULT_QUERY
    )

    val photos = currentQuery.switchMap { queryString ->
        repository.searchPhoto(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        if (query.length < 3) {
            _queryStatus.postValue(QueryEvent(query, QueryStatus.ErrorToShort))
            return
        }
        if (query.searchQueryError()) {
            _queryStatus.postValue(QueryEvent(query, QueryStatus.ErrorFormat))
            return
        }
        _queryStatus.postValue(QueryEvent(query, QueryStatus.Valid))
        currentQuery.value = query
    }

    private fun insertPhoto(photo: PixbayPhoto) {
        viewModelScope.launch { repository.insertPhoto(photo.toDbItem()) }
    }

    private fun deletePhoto(photo: PixbayPhoto) {
        viewModelScope.launch { repository.deletePhoto(photo.toDbItem()) }
    }

    fun allFavouritesPhoto() = repository.observeAllPhotos()

    fun favouriteClick(photo: PixbayPhoto) {
        disposable.add(
            repository.containsItem(photo.id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    deletePhoto(photo)
                }, {
                    insertPhoto(photo)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        _bluetoothAdapter.value?.disable()
    }

    fun bluetoothClick() {

        repository.bluetoothClick()
    }

    private val _bluetoothAdapter = MutableLiveData<BluetoothAdapter?>()
        .apply { value = BluetoothAdapter.getDefaultAdapter() }
    val bluetoothAdapter: LiveData<BluetoothAdapter?>
        get() = _bluetoothAdapter

    private val _bluetoothOn = MutableLiveData<Boolean>()
    val bluetoothOn: LiveData<Boolean>
        get() = _bluetoothOn

    private val _devices = MutableLiveData<List<BluetoothDeviceDomain>>()
    val devices: LiveData<List<BluetoothDeviceDomain>>
        get() = _devices

    private val _deviceBonding = MutableLiveData<Boolean>()
    val deviceBonding: LiveData<Boolean>
        get() = _deviceBonding

    private val _broadcastReceivers = mapOf<BroadcastReceiver, IntentFilter>(
        Pair(
            BluetoothOnOffReceiver()
                .apply {
                    viewModelScope.launch {
                        onStateOn = {
                            _bluetoothOn.value = true
                            discover(bluetoothAdapter.value!!)
                        }
                    }
                },
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        ),
        Pair(
            BluetoothDiscoverReceiver()
                .apply {
                    viewModelScope.launch {
                        devicesS.subscribe { list ->
                            _devices.value = list.map {
                                val device = BluetoothDeviceDomain(it)
                                bluetoothAdapter.value?.let { bluetoothAdapter ->
                                    val bondedDevices = bluetoothAdapter.bondedDevices
                                    device.isPaired = bondedDevices.contains(it)
                                }
                                device
                            }
                        }
                    }
                },
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        ),
        Pair(
            BluetoothBondReceiver()
                .apply {
                    viewModelScope.launch {
                        deviceBondingS.subscribe {
                            _deviceBonding.value = it
                        }
                        deviceHasBeenBondedS.subscribe {
                            val domainDevices = _devices.value!!
                            _bluetoothAdapter.value?.let { bluetoothAdapter ->
                                val bondedDevices = bluetoothAdapter.bondedDevices
                                domainDevices.forEach { domainDevice ->
                                    domainDevice.isPaired = bondedDevices.contains(domainDevice.device)
                                }
                            }
                            _devices.postValue(domainDevices)
                        }
                    }
                },
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
    )

    val broadcastReceivers = MutableLiveData<Map<BroadcastReceiver, IntentFilter>>()

    fun setupReceivers() {
        broadcastReceivers.value = _broadcastReceivers
    }

    fun bondDevice(device: BluetoothDevice) {
        bluetoothAdapter.value?.cancelDiscovery()
        device.createBond()
    }

    fun refresh() = discover(bluetoothAdapter.value!!)

    companion object {
        private const val DEFAULT_QUERY = "cats"
        private const val CURRENT_QUERY = "CURRENT_QUERY"
    }
}
