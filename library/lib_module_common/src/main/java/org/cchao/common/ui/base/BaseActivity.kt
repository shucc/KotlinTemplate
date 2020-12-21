package org.cchao.common.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.cchao.common.ui.dialog.LoadingDialog

abstract class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())

        initData()
        bindEvent()
    }

    abstract fun bindLayout(): Int

    abstract fun initData()

    abstract fun bindEvent()

    fun showLoading() {
        showLoading(null)
    }

    fun showLoading(content: String?) {
        if (null != loadingDialog && loadingDialog!!.isVisible) {
            loadingDialog!!.setLoadingText(content)
        } else {
            loadingDialog = LoadingDialog.showLoading(supportFragmentManager, content)
        }
    }

    fun hideLoading() {
        if (null != loadingDialog) {
            loadingDialog!!.dismiss()
        }
    }
}