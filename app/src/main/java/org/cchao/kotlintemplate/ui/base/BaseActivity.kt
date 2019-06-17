package org.cchao.kotlintemplate.ui.base

import android.os.Bundle
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

abstract class BaseActivity<T : BasePresenter> : RxAppCompatActivity(), BaseView {

    protected lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
        presenter = bindPresenter()
        presenter.attachView(this)
    }

    abstract fun bindLayout(): Int

    abstract fun bindPresenter(): T

    abstract fun initData()

    abstract fun bindEvent()

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}