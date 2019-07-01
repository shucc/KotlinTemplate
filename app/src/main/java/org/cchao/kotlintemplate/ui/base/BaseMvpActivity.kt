package org.cchao.kotlintemplate.ui.base

import android.os.Bundle

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
abstract class BaseMvpActivity<T : BasePresenter> : BaseActivity(), BaseView {

    lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = bindPresenter()
        presenter.attachView(this)
        super.onCreate(savedInstanceState)
    }

    abstract fun bindPresenter(): T

    override fun onPause() {
        presenter.unSubscribeOnPause()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.unSubscribeOnDestroy()
        presenter.detachView()
        super.onDestroy()
    }
}