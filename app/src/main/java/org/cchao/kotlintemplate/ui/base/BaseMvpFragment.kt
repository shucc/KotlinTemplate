package org.cchao.kotlintemplate.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
abstract class BaseMvpFragment<T : BasePresenter> : BaseFragment(), BaseView {

    lateinit var presenter: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = bindPresenter()
        presenter.attachView(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun bindPresenter(): T

    override fun onPause() {
        presenter.unSubscribeOnPause()
        super.onPause()
    }

    override fun onDestroyView() {
        presenter.unSubscribeOnDestroy()
        presenter.detachView()
        super.onDestroyView()
    }
}