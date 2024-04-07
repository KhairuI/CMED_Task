package com.example.cmed_task.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.cmed_task.adapter.CharacterAdapter
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask2Binding
import com.example.cmed_task.model.CharacterItems
import com.example.cmed_task.model.CharacterModel
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.utils.DataState
import com.example.cmed_task.utils.LoadingDialog
import com.example.cmed_task.viewmodel.Task2ViewModel
import org.json.JSONException

class Task2Activity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteDataSource.buildApi(ApiService::class.java))


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

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

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

     private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }


}