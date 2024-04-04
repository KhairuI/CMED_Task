package com.example.cmed_task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmed_task.model.CharacterModel
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.utils.DataState
import kotlinx.coroutines.launch
import retrofit2.Response

class Task2ViewModel(private val repository: Task2Repository) : ViewModel() {


    // get Quote list
    private val _getCharacterListResponse : MutableLiveData<DataState<Response<CharacterModel>>> = MutableLiveData()
    val getCharacterListResponse : LiveData<DataState<Response<CharacterModel>>> get() = _getCharacterListResponse
    

    // get Quote list
    fun getCharacterList() = viewModelScope.launch {

        _getCharacterListResponse.value = DataState.Loading
        _getCharacterListResponse.value = repository.getCharacterList()
    }
    


}