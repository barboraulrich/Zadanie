package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.helpers.Evento
import com.example.zadanie.ui.viewmodels.data.MyLocation
import kotlinx.coroutines.launch

enum class Sort {
    TITLE_ASCENDING, TITLE_DESCENDING,
    GUESTS_ASCENDING, GUESTS_DESCENDING,
    DISTANCE_ASCENDING, DISTANCE_DESCENDING
}
//Ascending means smallest to largest, 0 to 9, and/or A to Z
//Descending means largest to smallest, 9 to 0, and/or Z to A.

class BarsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)

    private var _sortType: MutableLiveData<Sort> = MutableLiveData( Sort.TITLE_ASCENDING)

    val bars: LiveData<List<BarItem>> = Transformations.switchMap(_sortType) { sort ->
        liveData {
            loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            loading.postValue(false)
            if(sort == Sort.DISTANCE_ASCENDING || sort == Sort.DISTANCE_DESCENDING)
            {
                emitSource(repository.dbSortedByDistance(sort, myLocation.value!!))
            }
            else
            {
                emitSource(repository.dbBars(sort))
            }
        }
    }

    fun sortList(sortType : Sort){
        _sortType.value = sortType
    }

    fun refreshData(){
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}