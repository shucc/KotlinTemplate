package org.cchao.kotlintemplate.ui.activity

import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.kotlintemplate.ui.activity.presenter.MainPresenter
import org.cchao.kotlintemplate.ui.base.BaseActivity

class MainActivity : BaseActivity<MainContract.Presenter>(), MainContract.View {

    override fun bindLayout(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindPresenter(): MainContract.Presenter {
        return MainPresenter()
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun testView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}