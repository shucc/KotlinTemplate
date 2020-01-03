package org.cchao.kotlintemplate.ui.activity.presenter

import org.cchao.common.expansion.bindToDestroy
import org.cchao.http.HttpUtils
import org.cchao.kotlintemplate.model.TestModel
import org.cchao.kotlintemplate.model.body.TestBody
import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.common.ui.base.BasePresenterImpl

class MainPresenter : BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    override fun testPresenter() {
        HttpUtils.getData(TestBody("fdgdg"), TestModel::class.java)
                .subscribe({

                }, {

                }).bindToDestroy(this)
    }
}