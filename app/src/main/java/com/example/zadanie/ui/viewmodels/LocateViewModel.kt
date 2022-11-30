package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.helpers.Evento
import com.example.zadanie.ui.viewmodels.data.MyLocation
import com.example.zadanie.ui.viewmodels.data.NearbyBar
import kotlinx.coroutines.launch

class LocateViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _loading = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _myLocation = MutableLiveData<MyLocation>(null)
    val myLocation: MutableLiveData<MyLocation> get() = _myLocation

    private val _myBar = MutableLiveData<NearbyBar>(null)
    val myBar: MutableLiveData<NearbyBar> get() = _myBar

    private val _checkedIn = MutableLiveData<Evento<Boolean>>()
    val checkedIn: LiveData<Evento<Boolean>>
        get() = _checkedIn


    private val _bars : LiveData<List<NearbyBar>> = myLocation.switchMap {
        liveData {
            _loading.postValue(true)
            it?.let {
                val b = repository.apiNearbyBars(it.lat,it.lon,{_message.postValue(Evento(it))})
                emit(b)
                if (myBar.value==null){
                    _myBar.postValue(b.firstOrNull())
                }
            } ?: emit(listOf())
            _loading.postValue(false)
        }
    }

    val bars: LiveData<List<NearbyBar>> get() = _bars

    fun checkMe(){
        viewModelScope.launch {
            _loading.postValue(true)
            myBar.value?.let {
                repository.apiBarCheckin(
                    it,
                    {_message.postValue(Evento(it))},
                    {_checkedIn.postValue(Evento(it))})
            }
            _loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}