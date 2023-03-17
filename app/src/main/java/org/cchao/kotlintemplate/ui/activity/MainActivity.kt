package org.cchao.kotlintemplate.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.activity_main.*
import org.cchao.common.expansion.clickWithTrigger
import org.cchao.common.expansion.showToast
import org.cchao.common.ui.base.BaseMvpActivity
import org.cchao.common.utils.L
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.activity.contract.MainContract
import org.cchao.kotlintemplate.ui.activity.presenter.MainPresenter
import org.cchao.kotlintemplate.ui.fragment.MainFragment

class MainActivity : BaseMvpActivity<MainContract.Presenter>(), MainContract.View {

    companion object {
        var secondActivity: SecondActivity? = null

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun bindLayout(): Int {
        return R.layout.activity_main
    }

    override fun bindPresenter(): MainContract.Presenter {
        return MainPresenter()
    }

    override fun initData() {
//        showLoading()
//        presenter.getData()
        iv_data.load("http://file02.16sucai.com/d/file/2015/0408/779334da99e40adb587d0ba715eca102.jpg") {
            transformations(CircleCropTransformation())
        }
        if (null == supportFragmentManager.findFragmentByTag("MainFragment")) {
            val trans = supportFragmentManager.beginTransaction()
            trans.add(R.id.fl_fragment, MainFragment.newInstance(), "MainFragment")
            trans.commitAllowingStateLoss()
        }
    }

    override fun bindEvent() {
        iv_data.clickWithTrigger { SecondActivity.start(this) }
    }

    override fun onGetDataSuccess(data: String) {
        hideLoading()
        tv_data.text = data
    }

    override fun onGetDataError(msg: String) {
        hideLoading()
        showToast(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.d("Activity-->onCreate")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        L.d("Activity-->onNewIntent")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        L.d("Activity-->onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        L.d("Activity-->onRestoreInstanceState")
    }

    override fun onStart() {
        super.onStart()
        L.d("Activity-->onStart")
    }

    override fun onRestart() {
        super.onRestart()
        L.d("Activity-->onRestart")
    }

    override fun onResume() {
        super.onResume()
        L.d("Activity-->onResume")
    }

    override fun onPause() {
        super.onPause()
        L.d("Activity-->onPause")
    }

    override fun onStop() {
        super.onStop()
        L.d("Activity-->onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d("Activity-->onDestroy")
    }
}