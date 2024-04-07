package com.example.cmed_task.views

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.view.View
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask1Binding
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.TaskRepository
import com.example.cmed_task.service.DownloadService
import com.example.cmed_task.utils.AppConstants
import com.example.cmed_task.utils.AppConstants.CLOSE_NOTIFICATION
import com.example.cmed_task.utils.AppConstants.DOWNLOAD_PROGRESS
import com.example.cmed_task.utils.AppConstants.SHOW_NOTIFICATION
import com.example.cmed_task.viewmodel.TaskViewModel

class Task1Activity : BaseActivity<TaskViewModel, TaskRepository>() {

    override fun getViewModel() = TaskViewModel::class.java
    override fun getRepository() =
        TaskRepository(remoteVideoSource.buildApi(ApiService::class.java))


    // init all variable
    private lateinit var binding: ActivityTask1Binding
    private lateinit var intent: Intent

    private var mService: Messenger? = null

    private var bound: Boolean = false


    override fun getLayoutResourceId(): View {
        binding = ActivityTask1Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

        binding.btnTask2.setOnClickListener {
            invokeActivity(Task2Activity::class.java)
        }

        binding.btnDownload.setOnClickListener {
            intent = Intent(this, DownloadService::class.java).also { intent ->
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }
            startService(intent)
        }

    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mService = null
            bound = false
        }
    }

    private fun alertNotification(status: Int) {
        if (!bound) return
        val msg: Message = Message.obtain(null, status, 0, 0)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    private val mTimeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            val value = intent.extras?.getInt(DOWNLOAD_PROGRESS)

            value?.let { progress ->
                binding.progressBar.progress = progress
                binding.tvProgress.text = "$progress%"

                if (progress > -1) {
                    binding.btnDownload.apply {
                        text = "Downloading..."
                        isEnabled = false
                    }
                }
                if (progress == 100) {
                    binding.btnDownload.apply {
                        text = "Download"
                        isEnabled = true
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mTimeReceiver, IntentFilter(AppConstants.INTENT_FILE))
        alertNotification(CLOSE_NOTIFICATION)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mTimeReceiver)
        alertNotification(SHOW_NOTIFICATION)
    }

    override fun onDestroy() {
        alertNotification(CLOSE_NOTIFICATION)
        stopService(Intent(this, DownloadService::class.java))
        super.onDestroy()
    }

    override fun onStop() {
        try {
            unregisterReceiver(mTimeReceiver)
        } catch (e: Exception) {

        }
        super.onStop()
    }

}