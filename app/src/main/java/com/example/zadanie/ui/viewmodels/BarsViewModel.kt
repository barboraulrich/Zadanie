package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.helpers.Evento
import com.example.zadanie.ui.viewmodels.data.MyLocation
import kotlinx.coroutines.launch

enum class Sort {
    ASC_TITLE, DESC_TITLE,
    ASC_G, DESC_G,
    ASC_DIST, DESC_DIST
}

class BarsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _loading = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _myLocation = MutableLiveData<MyLocation>(null)
    val myLocation: MutableLiveData<MyLocation> get() = _myLocation

    private var _sortType: MutableLiveData<Sort> = MutableLiveData( Sort.ASC_TITLE)
    val sortType: LiveData<Sort> get() = _sortType

    private val _bars: LiveData<List<BarItem>> = Transformations.switchMap(_sortType) { sort ->
        liveData {
            _loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            _loading.postValue(false)
            if(sort == Sort.ASC_DIST || sort == Sort.DESC_DIST)
            {
                emitSource(repository.dbSortedByDistance(sort, myLocation.value!!))
            }
            else
            {
                emitSource(repository.dbBars(sort))
            }
        }
    }
    val bars: LiveData<List<BarItem>> get() = _bars

    fun sortList(sortType : Sort){
        _sortType.value = sortType
    }

    fun refreshData(){
        viewModelScope.launch {
            _loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            _loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}