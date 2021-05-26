package org.cchao.common.expansion

import io.reactivex.rxjava3.disposables.Disposable
import org.cchao.common.ui.base.BasePresenter

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
