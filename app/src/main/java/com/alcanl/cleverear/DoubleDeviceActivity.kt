package com.alcanl.cleverear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alcanl.cleverear.databinding.ActivityDoubleDeviceBinding

class DoubleDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoubleDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityDoubleDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}