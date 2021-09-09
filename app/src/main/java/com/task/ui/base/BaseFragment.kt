package com.task.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    companion object {
        private val TAG = BaseFragment::class.java.simpleName
    }

    protected lateinit var rootView: View
    protected abstract fun getViewBinding(): VB
    protected abstract fun initializeObject()
    protected abstract fun initializeToolBar()
    protected abstract fun initializeCallbackListener()
    protected abstract fun addTextChangedListener()
    protected abstract fun setOnClickListener()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view

        initializeObject()
        initializeToolBar()
        initializeCallbackListener()
        addTextChangedListener()
        setOnClickListener()
    }
}