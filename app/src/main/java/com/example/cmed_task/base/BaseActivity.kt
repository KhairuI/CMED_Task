package com.example.cmed_task.base

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cmed_task.network.RemoteDataSource
import com.example.cmed_task.network.RemoteVideoSource
import com.example.cmed_task.viewmodel.NetworkViewModelFactory
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
abstract class BaseActivity<VM : ViewModel, R : BaseRepository> : AppCompatActivity() {


    lateinit var viewModel: VM
    protected val remoteDataSource = RemoteDataSource()
    protected val remoteVideoSource = RemoteVideoSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLayoutResourceId()?.let { setContentView(it) }
        val factory = NetworkViewModelFactory(getRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]
        init(savedInstanceState)
    }

    protected abstract fun init(savedInstanceState: Bundle?)

    abstract fun getViewModel(): Class<VM>

    abstract fun getRepository(): R

    protected abstract fun getLayoutResourceId(): View?


    fun invokeActivity(cls: Class<*>?) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    fun invokeActivity(cls: Class<*>?, key: String, value: String) {
        val intent = Intent(this, cls).apply {
            flags = FLAG_ACTIVITY_CLEAR_TASK
            putExtra(key, value)
        }
        startActivity(intent)
    }


    fun invokeActivityAndFinish(cls: Class<*>?) {
        val intent = Intent(this, cls).apply {
            flags = FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    fun <T : Serializable?> getSerializable(intent: Intent, key: String, className: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= 33)
            intent.getSerializableExtra(key, className)!!
        else
            intent.getSerializableExtra(key) as T
    }


}