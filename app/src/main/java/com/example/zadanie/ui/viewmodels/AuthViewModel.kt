package com.example.zadanie.ui.viewmodels

import androidx.lifecycle.*
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.api.UserResponse
import com.example.zadanie.helpers.Evento
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest

class AuthViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val user= MutableLiveData<UserResponse>(null)

    val loading = MutableLiveData(false)

    fun login(name: String, password: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiUserLogin(
//                name,md5(password),
                name,password,
                { _message.postValue(Evento(it)) },
                { user.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    fun signup(name: String, password: String){
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiUserCreate(
//                name,md5(password),
                name,password,
                { _message.postValue(Evento(it)) },
                { user.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}

    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}