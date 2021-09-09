package com.task.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    companion object {
        private val TAG: String = BaseActivity::class.java.simpleName
    }

    protected lateinit var binding: VB

    protected abstract fun getViewBinding(): VB
    protected abstract fun initializeObject()
    protected abstract fun initializeToolBar()
    protected abstract fun initializeCallbackListener()
    protected abstract fun addTextChangedListener()
    protected abstract fun setOnClickListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewBinding()
        setContentView(binding.root)

        initializeObject()
        initializeToolBar()
        initializeCallbackListener()
        addTextChangedListener()
        setOnClickListener()
    }
}