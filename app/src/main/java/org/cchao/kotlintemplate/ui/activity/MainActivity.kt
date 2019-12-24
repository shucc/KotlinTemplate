package org.cchao.kotlintemplate.ui.activity

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.kotlintemplate.ui.activity.presenter.MainPresenter
import org.cchao.kotlintemplate.ui.base.BaseMvpActivity

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
        text_test.setOnClickListener {
            showLoading()
            startActivity(Intent(this, SecondActivity::class.java))
        }
        text_test2.setOnClickListener {

        }
    }

    override fun testView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}