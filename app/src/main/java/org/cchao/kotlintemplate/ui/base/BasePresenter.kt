package org.cchao.kotlintemplate.ui.base

interface BasePresenter {

    fun attachView(baseView: BaseView)

    fun detachView()
}