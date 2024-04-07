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
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityTask1Binding
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.service.MyService
import com.example.cmed_task.utils.AppConstants
import com.example.cmed_task.utils.AppConstants.CLOSE_NOTIFICATION
import com.example.cmed_task.utils.AppConstants.DOWNLOAD_PROGRESS
import com.example.cmed_task.utils.AppConstants.SHOW_NOTIFICATION
import com.example.cmed_task.utils.DataState
import com.example.cmed_task.utils.LoadingDialog
import com.example.cmed_task.viewmodel.Task2ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

class Task1Activity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteVideoSource.buildApi(ApiService::class.java))


    // init all variable
    private lateinit var binding: ActivityTask1Binding
    private lateinit var loadingDialog: LoadingDialog
    private val timestamp = System.currentTimeMillis()
    private lateinit var intent: Intent

    private var mService: Messenger? = null

    private var bound: Boolean = false


    override fun getLayoutResourceId(): View {
        binding = ActivityTask1Binding.inflate(layoutInflater)
        return binding.root
    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            mService = null
            bound = false
        }
    }

    private fun sayHello(status: Int) {
        if (!bound) return
        // Create and send a message to the service, using a supported 'what' value.
        val msg: Message = Message.obtain(null, status, 0, 0)
        try {
            mService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }


    override fun init(savedInstanceState: Bundle?) {


        // loading
        loadingDialog = LoadingDialog(this)

        // observe all response
        //  observeVideoResponse()

        binding.btnTask2.setOnClickListener {
            invokeActivity(Task2Activity::class.java)
        }

        binding.btnDownload.setOnClickListener {
            intent = Intent(this, MyService::class.java).also { intent ->
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }
            startService(intent)
        }

    }

    private val mTimeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {

            val value = intent.extras?.getInt(DOWNLOAD_PROGRESS)
            Log.d("xxx", "onReceive: $value")

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
        sayHello(CLOSE_NOTIFICATION)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mTimeReceiver)
        sayHello(SHOW_NOTIFICATION)
    }

    override fun onDestroy() {
        stopService(Intent(this, MyService::class.java))
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