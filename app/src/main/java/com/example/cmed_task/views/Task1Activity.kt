package com.example.cmed_task.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask1Binding
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.utils.DataState
import com.example.cmed_task.utils.LoadingDialog
import com.example.cmed_task.viewmodel.Task2ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import java.io.File

class Task1Activity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteVideoSource.buildApi(ApiService::class.java))


    // init all variable
    private lateinit var binding: ActivityTask1Binding
    private lateinit var loadingDialog: LoadingDialog
    private val timestamp = System.currentTimeMillis()

    override fun getLayoutResourceId(): View {
        binding = ActivityTask1Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

        // loading
        loadingDialog = LoadingDialog(this)

        // observe all response
        observeVideoResponse()

        binding.btnTask2.setOnClickListener {
            invokeActivity(Task2Activity::class.java)
        }

        binding.btnDownload.setOnClickListener { viewModel.getVideo() }

    }

    private fun observeVideoResponse() {
        viewModel.getVideoResponse.observe(this) {

            when (it) {

                is DataState.Success -> {

                    try {

                        // gone loading
                        if (loadingDialog.isShowing) loadingDialog.dismiss()

                        // get data
                        val body = it.value
                        saveFile(body, timestamp.toString())


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

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveFile(body: ResponseBody, fileName: String) {

        binding.btnDownload.apply {
            text = "Downloading..."
            isEnabled = false
        }

        GlobalScope.launch(Dispatchers.IO) {

            val downloadFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationFile = File(downloadFolder.absolutePath, "file_${fileName}.zip")

            val fileSize = body.contentLength()
            val inputStream = body.byteStream()
            val outputStream = destinationFile.outputStream()

            var progressBytes: Long = 0
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = inputStream.read(buffer)
            while (bytes >= 0) {

                progressBytes += bytes
                val downloadProgress = (progressBytes.toFloat() / fileSize.toFloat() * 100).toInt()

                withContext(Dispatchers.Main) {
                    binding.progressBar.progress = downloadProgress
                    binding.tvProgress.text = "$downloadProgress%"
                }

                outputStream.write(buffer, 0, bytes)
                bytes = inputStream.read(buffer)

            }
            outputStream.close()
            inputStream.close()

            withContext(Dispatchers.Main) {
                binding.btnDownload.apply {
                    text = "Download"
                    isEnabled = true
                }
            }
        }
    }

}