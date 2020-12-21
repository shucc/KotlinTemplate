package org.cchao.kotlintemplate.ui.activity

import kotlinx.android.synthetic.main.activity_main.*
import org.cchao.common.expansion.clickWithTrigger
import org.cchao.common.expansion.showToast
import org.cchao.common.ui.base.BaseMvpActivity
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.kotlintemplate.ui.activity.presenter.MainPresenter

class MainActivity : BaseMvpActivity<MainContract.Presenter>(), MainContract.View {

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun bindPresenter(): MainContract.Presenter {
        return MainPresenter()
    }

    override fun initData() {
        presenter.testPresenter()
    }

    override fun bindEvent() {
        text_test.clickWithTrigger {
            showLoading()
            it.postDelayed({ showLoading("test") }, 2000)
        }
        text_test2.clickWithTrigger {
            showToast("hhehe")
        }
    }

    override fun testView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}