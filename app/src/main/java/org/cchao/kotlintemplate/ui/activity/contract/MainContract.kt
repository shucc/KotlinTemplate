package org.cchao.kotlintemplate.ui.activity.contract

import org.cchao.common.ui.base.BasePresenter
import org.cchao.common.ui.base.BaseView

interface MainContract {

    interface View : BaseView {
        fun testView()
    }

    interface Presenter : BasePresenter {
        fun testPresenter()
    }
}