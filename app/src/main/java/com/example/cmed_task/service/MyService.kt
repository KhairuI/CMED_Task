package com.example.cmed_task.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.core.app.NotificationCompat
import com.example.cmed_task.R
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.network.RemoteVideoSource
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.utils.AppConstants.CLOSE_NOTIFICATION
import com.example.cmed_task.utils.AppConstants.DOWNLOAD_PROGRESS
import com.example.cmed_task.utils.AppConstants.INTENT_FILE
import com.example.cmed_task.utils.AppConstants.SHOW_NOTIFICATION
import com.example.cmed_task.utils.DataState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONException
import java.io.File


class MyService : Service() {

    private val intent = Intent(INTENT_FILE)
    private val fileName = System.currentTimeMillis().toString()
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private val CHANNEL_ID = "101"
    private val NOTIFICATION_ID = 1
    private var isNotificationShow = false
    private val repository = Task2Repository(RemoteVideoSource().buildApi(ApiService::class.java))

    private lateinit var mMessenger: Messenger

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SHOW_NOTIFICATION -> isNotificationShow = true
                CLOSE_NOTIFICATION -> isNotificationShow = false
                else -> super.handleMessage(msg)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch(Dispatchers.IO) {
            when (val res = repository.getVideo()) {
                is DataState.Success -> {

                    try {

                        // get data
                        val body = res.value
                        saveFile(body)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                is DataState.Loading -> {}

                is DataState.Error -> {}
            }
        }

    }

    private fun saveFile(body: ResponseBody) {

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
                intent.putExtra(DOWNLOAD_PROGRESS, downloadProgress)
                sendBroadcast(intent)
                updateNotification(downloadProgress.toString())

                outputStream.write(buffer, 0, bytes)
                bytes = inputStream.read(buffer)

            }
            outputStream.close()
            inputStream.close()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun showNotification(content: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Background Notification",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download Progress")
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("$content%")
            .build()
    }

    private fun updateNotification(progressValue: String) {
        notification = showNotification(progressValue)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (isNotificationShow) {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        mMessenger = Messenger(handler)
        return mMessenger.binder
    }
}