package com.example.cmed_task.views

import android.os.Bundle
import android.view.View
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityDetailsBinding
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.viewmodel.Task2ViewModel

class DetailsActivity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteDataSource.buildApi(ApiService::class.java))

    // init all variable
    private lateinit var binding: ActivityDetailsBinding

    override fun getLayoutResourceId(): View {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

    }
}