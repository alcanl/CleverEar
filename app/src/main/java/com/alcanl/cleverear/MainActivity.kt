package com.alcanl.cleverear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivityMainBinding
import com.alcanl.cleverear.viewmodel.MainActivityListenersViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initBinding()
    }
    fun buttonSingleDeviceClick()
    {
        Intent(this, EarSideSelectionActivity::class.java).apply { startActivity(this) }
    }
    fun buttonDoubleDeviceClick()
    {
        Intent(this, ConnectionActivity::class.java).apply { startActivity(this) }
    }
    private fun initBinding()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.viewModel = MainActivityListenersViewModel(this)
    }
}