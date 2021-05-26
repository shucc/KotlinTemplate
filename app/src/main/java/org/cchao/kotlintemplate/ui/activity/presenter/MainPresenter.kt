package org.cchao.kotlintemplate.ui.activity.presenter

import org.cchao.common.expansion.bindToDestroy
import org.cchao.common.ui.base.BasePresenterImpl
import org.cchao.common.utils.JsonUtils
import org.cchao.http.HttpUtils
import org.cchao.kotlintemplate.model.body.TestBody
import org.cchao.kotlintemplate.ui.activity.contract.MainContract

class MainPresenter : BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    override fun getData() {
        HttpUtils.getBaseData(TestBody())
                .subscribe({
                    getPresenterView().onGetDataSuccess(JsonUtils.toString(it))
                }, {
                    getPresenterView().onGetDataError(it.message!!)
                }).bindToDestroy(this)
    }
}