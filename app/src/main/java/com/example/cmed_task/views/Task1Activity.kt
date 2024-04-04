package com.example.cmed_task.views

import android.os.Bundle
import android.view.View
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask1Binding
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.viewmodel.Task2ViewModel

class Task1Activity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteDataSource.buildApi(ApiService::class.java))


    // init all variable
    private lateinit var binding: ActivityTask1Binding

    override fun getLayoutResourceId(): View {
        binding = ActivityTask1Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

        binding.btnTask2.setOnClickListener {
            invokeActivity(Task2Activity::class.java)
        }

    }
}