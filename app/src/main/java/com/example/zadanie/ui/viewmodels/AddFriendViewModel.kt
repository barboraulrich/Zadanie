package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zadanie.data.DataRepository
import com.example.zadanie.helpers.Evento
import kotlinx.coroutines.launch

class AddFriendViewModel(private val repository: DataRepository): ViewModel()
{
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    private val _isAdded = MutableLiveData<Boolean>(false)
    val isAdded: LiveData<Boolean>
        get() = _isAdded

    fun addFriend(username: String)
    {
        viewModelScope.launch {
            repository.addFriend(
                username,
                {_message.postValue(Evento(it))},
                {_isAdded.postValue(it)}
            )
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}


}