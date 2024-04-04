package com.example.cmed_task.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cmed_task.base.BaseRepository
import com.example.cmed_task.repository.Task2Repository

@Suppress("UNCHECKED_CAST")
class NetworkViewModelFactory (
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(Task2ViewModel::class.java) -> Task2ViewModel(repository as Task2Repository) as T

            else -> throw IllegalArgumentException("NetworkViewModelFactory Not Found")
        }
    }

}