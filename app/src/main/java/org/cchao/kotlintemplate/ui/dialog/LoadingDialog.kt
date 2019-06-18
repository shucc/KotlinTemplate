package org.cchao.kotlintemplate.ui.dialog

import android.content.Context
import kotlinx.android.synthetic.main.dialog_loading.*
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.ui.base.BaseDialog

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
class LoadingDialog(context: Context) : BaseDialog(context) {

    companion object {

        fun show(context: Context): LoadingDialog {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.setCanceledOnTouchOutside(false)
            loadingDialog.show()
            return loadingDialog
        }
    }

    override fun bindLayout(): Int {
        return R.layout.dialog_loading
    }

    override fun initData() {

    }

    override fun bindEvent() {

    }

    fun setLoadingText(content: String?) {
        if (null == content) {
            dialog_loading_text.text = context.resources.getString(R.string.loading_default)
            return
        }
        dialog_loading_text.text = content
    }
}