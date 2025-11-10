package com.tb.pdfly.page.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.tb.pdfly.parameter.myEnableEdgeToEdge
import com.tb.pdfly.parameter.setDensity
import com.tb.pdfly.parameter.updateResources

abstract class BaseActivity<T : ViewBinding>(private val inflate: (layoutInflater: LayoutInflater) -> T) : AppCompatActivity() {

    protected lateinit var binding: T
    var printContext: Context? = null

    override fun attachBaseContext(newBase: Context?) {
        printContext = newBase
        if (null != newBase) super.attachBaseContext(newBase.updateResources()) else super.attachBaseContext(null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        setDensity()
        initView()
    }

    protected open fun initView() = Unit

    override fun onAttachedToWindow() = myEnableEdgeToEdge()
}