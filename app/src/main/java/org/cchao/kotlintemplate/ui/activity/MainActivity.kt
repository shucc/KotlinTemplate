package org.cchao.kotlintemplate.ui.activity

import coil.load
import coil.transform.CircleCropTransformation
import kotlinx.android.synthetic.main.activity_main.*
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
        showLoading()
        presenter.getData()
        iv_data.load("http://file02.16sucai.com/d/file/2015/0408/779334da99e40adb587d0ba715eca102.jpg") {
            transformations(CircleCropTransformation())
        }
    }

    override fun bindEvent() {

    }

    override fun onGetDataSuccess(data: String) {
        hideLoading()
        tv_data.text = data
    }

    override fun onGetDataError(msg: String) {
        hideLoading()
        showToast(msg)
    }
}