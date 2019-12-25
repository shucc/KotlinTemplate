package org.cchao.kotlintemplate.ui.activity.presenter

import org.cchao.http.HttpUtils
import org.cchao.kotlintemplate.expansion.bindToDestroy
import org.cchao.kotlintemplate.model.TestModel
import org.cchao.kotlintemplate.model.body.TestBody
import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.kotlintemplate.ui.base.BasePresenterImpl

class MainPresenter : BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    override fun testPresenter() {
        HttpUtils.getData(TestBody("fdgdg"), TestModel::class.java)
                .subscribe({

                }, {

                }).bindToDestroy(this)
    }
}