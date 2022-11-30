package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.db.model.BarItem
import com.example.zadanie.data.db.model.FriendItem
import com.example.zadanie.helpers.Evento
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _friends: LiveData<List<FriendItem>?> =
        liveData {
            _loading.postValue(true)
            repository.apiFriendsList { _message.postValue(Evento(it)) }
            _loading.postValue(false)
            emitSource(repository.dbFriends())
        }
    val friends: LiveData<List<FriendItem>?> get() = _friends


    fun refreshData(){
        viewModelScope.launch {
            _loading.postValue(true)
            repository.apiFriendsList { _message.postValue(Evento(it)) }
            _loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}