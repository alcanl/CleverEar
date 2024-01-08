package com.alcanl.cleverear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alcanl.cleverear.databinding.ActivityEarSideSelectionBinding
import com.alcanl.cleverear.helpers.BUNDLE_EAR_SIDE_KEY
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
        Intent(this, ConnectionActivity::class.java).apply {
            putExtra(BUNDLE_EAR_SIDE_KEY, LEFT)
            setResult(RESULT_OK, this)
            startActivity(this)
        }
    }
    fun rightEarButtonClicked()
    {
        Intent(this, ConnectionActivity::class.java).apply {
            putExtra(BUNDLE_EAR_SIDE_KEY, RIGHT)
            setResult(RESULT_OK, this)
            startActivity(this)
        }
    }
}