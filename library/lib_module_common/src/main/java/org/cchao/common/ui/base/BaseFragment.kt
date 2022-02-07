package org.cchao.common.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.cchao.common.ui.dialog.LoadingDialog

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
abstract class BaseFragment : Fragment() {

    private var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(container!!.context).inflate(bindLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            loadingDialog = LoadingDialog.showLoading(activity!!.supportFragmentManager, content)
        }
    }

    fun hideLoading() {
        if (null != loadingDialog) {
            loadingDialog!!.dismiss()
        }
    }

    override fun onDestroy() {
        hideLoading()
        if (null != loadingDialog) {
            loadingDialog = null
        }
        super.onDestroy()
    }
}