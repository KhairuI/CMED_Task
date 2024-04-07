package com.example.cmed_task.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cmed_task.base.BaseRepository
import com.example.cmed_task.repository.TaskRepository

@Suppress("UNCHECKED_CAST")
class NetworkViewModelFactory (
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(TaskViewModel::class.java) -> TaskViewModel(repository as TaskRepository) as T

            else -> throw IllegalArgumentException("NetworkViewModelFactory Not Found")
        }
    }

}