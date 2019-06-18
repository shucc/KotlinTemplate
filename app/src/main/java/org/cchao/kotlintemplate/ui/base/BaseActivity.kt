package org.cchao.kotlintemplate.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.cchao.kotlintemplate.expansion.clearLoading
import org.cchao.kotlintemplate.expansion.hideLoading

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())

        initData()
        bindEvent()
    }

    abstract fun bindLayout(): Int

    abstract fun initData()

    abstract fun bindEvent()

    override fun onDestroy() {
        hideLoading()
        clearLoading()
        super.onDestroy()
    }
}