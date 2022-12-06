package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.helpers.Evento
import com.example.zadanie.ui.viewmodels.data.NearbyBar
import com.example.zadanie.ui.widget.detailList.BarDetailItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading


    private val _bar = MutableLiveData<NearbyBar>(null)
    val bar: MutableLiveData<NearbyBar> get() = _bar

    private val _type = bar.map { it?.tags?.getOrDefault("amenity", "") ?: "" }
    val type get() = _type

    private val _details: LiveData<List<BarDetailItem>> = bar.switchMap {
        liveData {
            it?.let {
                emit(it.tags.map {
                    BarDetailItem(it.key, it.value)
                })
            } ?: emit(emptyList<BarDetailItem>())
        }
    }
    val details: LiveData<List<BarDetailItem>> get() = _details



    fun loadBar(id: String) {
        if (id.isBlank())
            return
        viewModelScope.launch {
            _loading.postValue(true)
            bar.postValue(repository.apiBarDetail(id) { _message.postValue(Evento(it)) })
            _loading.postValue(false)
        }
    }

    fun getBarItem(id: String): LiveData<BarItem>
    {
        return repository.getBarById(id).asLiveData()
    }
}