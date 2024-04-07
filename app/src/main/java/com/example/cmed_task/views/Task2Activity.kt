package com.example.cmed_task.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.cmed_task.adapter.CharacterAdapter
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask2Binding
import com.example.cmed_task.model.CharacterItems
import com.example.cmed_task.model.CharacterModel
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.TaskRepository
import com.example.cmed_task.utils.DataState
import com.example.cmed_task.utils.LoadingDialog
import com.example.cmed_task.viewmodel.TaskViewModel
import org.json.JSONException

class Task2Activity : BaseActivity<TaskViewModel, TaskRepository>() {

    override fun getViewModel() = TaskViewModel::class.java
    override fun getRepository() =
        TaskRepository(remoteDataSource.buildApi(ApiService::class.java))


    // init all variable
    private lateinit var binding: ActivityTask2Binding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var characterAdapter: CharacterAdapter

    override fun getLayoutResourceId(): View {
        binding = ActivityTask2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

        // loading
        loadingDialog = LoadingDialog(this)

        // observe all response
        observeCharacterListResponse()


        // get all Character
        viewModel.getCharacterList()

        binding.ivBack.setOnClickListener { invokeActivityAndFinish(Task1Activity::class.java) }

    }

    private fun observeCharacterListResponse() {

        viewModel.getCharacterListResponse.observe(this) {

            when (it) {

                is DataState.Success -> {

                    try {

                        // gone loading
                        if (loadingDialog.isShowing) loadingDialog.dismiss()

                        // get data
                        val body = it.value.body()
                        body?.let { data ->
                            if (data.isNotEmpty()) {
                                setData(data)
                            }
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                is DataState.Loading -> {
                    if (!loadingDialog.isShowing) loadingDialog.show()
                }

                is DataState.Error -> {
                    // gone loading
                    if (loadingDialog.isShowing) loadingDialog.dismiss()
                    if (it.isNetworkError) Toast.makeText(
                        this,
                        "Network Error",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

        }
    }

    private fun setData(data: CharacterModel) {

        if (!data.isEmpty()) {
            binding.tvNoData.visibility = View.GONE
            characterAdapter = CharacterAdapter(this, object : CharacterAdapter.OnClickListener {
                override fun onClick(character: CharacterItems) {
                    val intent = Intent(this@Task2Activity, DetailsActivity::class.java).apply {
                        putExtra("character", character)
                    }
                    startActivity(intent)
                }
            })

            binding.rvCharacter.apply {
                setHasFixedSize(true)
                adapter = characterAdapter
            }
            characterAdapter.updateList(data as MutableList<CharacterItems>)
        }

    }

}