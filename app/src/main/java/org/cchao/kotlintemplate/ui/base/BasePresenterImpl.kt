package org.cchao.kotlintemplate.ui.base

open class BasePresenterImpl<T : BaseView> : BasePresenter {

    private var presenterView: T? = null

    override fun attachView(baseView: BaseView) {
        this.presenterView = (baseView as T)
    }

    override fun detachView() {
        presenterView = null
    }

    protected fun getPresenterView(): T {
        return presenterView!!
    }
}