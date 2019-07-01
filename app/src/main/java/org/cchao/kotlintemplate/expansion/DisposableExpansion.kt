package org.cchao.kotlintemplate.expansion

import io.reactivex.disposables.Disposable
import org.cchao.kotlintemplate.ui.base.BasePresenter

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
fun Disposable.bindToPause(presenter: BasePresenter) {
    presenter.getPauseCompositeDisposable().add(this)
}

fun Disposable.bindToDestroy(presenter: BasePresenter) {
    presenter.getDestroyCompositeDisposable().add(this)
}
