package org.cchao.common.ui.base

import io.reactivex.rxjava3.disposables.CompositeDisposable

interface BasePresenter {

    fun attachView(baseView: BaseView)

    fun detachView()

    fun getPauseCompositeDisposable(): CompositeDisposable

    fun unSubscribeOnPause()

    fun getDestroyCompositeDisposable(): CompositeDisposable

    fun unSubscribeOnDestroy()
}