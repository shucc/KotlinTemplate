package org.cchao.kotlintemplate.ui.base

import android.os.Bundle
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import org.cchao.kotlintemplate.expansion.clearLoading
import org.cchao.kotlintemplate.expansion.hideLoading

abstract class BaseActivity<T : BasePresenter> : RxAppCompatActivity(), BaseView {

    lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
        presenter = bindPresenter()
        presenter.attachView(this)

        initData()
        bindEvent()
    }

    abstract fun bindLayout(): Int

    abstract fun bindPresenter(): T

    abstract fun initData()

    abstract fun bindEvent()

    override fun onDestroy() {
        hideLoading()
        clearLoading()
        presenter.detachView()
        super.onDestroy()
    }
}