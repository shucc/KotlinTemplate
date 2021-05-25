package org.cchao.kotlintemplate.ui.activity

import android.view.View
import coil.load
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
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
        MainScope().launch {
            println("tag1 ${Thread.currentThread().name}")
            val text1 = async { test1() }
            val text2 = async { test2() }
            println("tag5 ${Thread.currentThread().name}")
            println(text1.await() + text2.await())
            println("tag6 ${Thread.currentThread().name}")
        }
        iv_test.load(R.mipmap.ic_launcher)
    }

    private suspend fun test1(): String {
        return withContext(Dispatchers.IO) {
            println("tag2 ${Thread.currentThread().name}")
            delay(100)
            "Hello"
        }
    }

    private suspend fun test2(): String {
        return withContext(Dispatchers.IO) {
            println("tag3 ${Thread.currentThread().name}")
            delay(100)
            "World"
        }
    }

    private suspend fun test(): String {
        return withContext(Dispatchers.IO) {
            println("tag4 ${Thread.currentThread().name}")
            delay(100)
            "Hello22"
        }
    }

    override fun bindEvent() {
        text_test.clickWithTrigger(View.OnClickListener {
            showLoading()
            it.postDelayed({ showLoading("test") }, 200)
        })
        text_test2.clickWithTrigger(View.OnClickListener { showToast("How are you!") })
    }

    override fun testView() {
    }
}