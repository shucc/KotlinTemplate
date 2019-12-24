package org.cchao.kotlintemplate.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_loading.*
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.base.BaseDialog

class LoadingDialog : BaseDialog() {

    private var content: String? = null

    companion object {
        const val KEY_CONTENT = "key_content"

        fun showLoading(fragmentManager: FragmentManager): LoadingDialog {
            return showLoading(fragmentManager, null)
        }

        fun showLoading(fragmentManager: FragmentManager, content: String?): LoadingDialog {
            val loadingDialog = LoadingDialog()
            if (!TextUtils.isEmpty(content)) {
                val bundle = Bundle()
                bundle.putString(KEY_CONTENT, content)
                loadingDialog.arguments = bundle
            }
            loadingDialog.show(fragmentManager, "LoadingDialog" + System.currentTimeMillis())
            return loadingDialog
        }
    }

    override fun bindLayout(): Int {
        return R.layout.dialog_loading
    }

    override fun initData() {
       if (null != arguments) {
           content = arguments!!.getString(KEY_CONTENT, activity!!.getString(R.string.loading_default))
       } else {
           content = activity!!.getString(R.string.loading_default)
       }
        dialog_loading_text.text = content!!
    }

    override fun bindEvent() {

    }
}