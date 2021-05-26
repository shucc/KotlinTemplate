package org.cchao.kotlintemplate.ui.activity.contract

import org.cchao.common.ui.base.BasePresenter
import org.cchao.common.ui.base.BaseView

interface MainContract {

    interface View : BaseView {

        fun onGetDataSuccess(data: String)

        fun onGetDataError(msg: String)
    }

    interface Presenter : BasePresenter {

        fun getData()
    }
}