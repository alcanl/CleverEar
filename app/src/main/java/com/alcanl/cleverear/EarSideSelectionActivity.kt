package com.alcanl.cleverear

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivityEarSideSelectionBinding
import com.alcanl.cleverear.helpers.EXTRA_KEY
import com.alcanl.cleverear.helpers.LEFT
import com.alcanl.cleverear.helpers.RIGHT
import com.alcanl.cleverear.viewmodel.EarSideSelectionActivityListenersViewModel

class EarSideSelectionActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityEarSideSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        initialize()
    }
    private fun initialize()
    {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ear_side_selection)
        mBinding.viewModel = EarSideSelectionActivityListenersViewModel(this)
    }
    fun leftEarButtonClicked()
    {
        Intent().putExtra(EXTRA_KEY, LEFT).also { Intent(this, SingleDeviceActivity::class.java)
            .apply { startActivity(this) } }

    }
    fun rightEarButtonClicked()
    {
        Intent().putExtra(EXTRA_KEY, RIGHT).also { Intent(this, SingleDeviceActivity::class.java)
            .apply { startActivity(this) } }
    }

}