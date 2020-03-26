package org.cchao.common.ui.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_prompt.*
import org.cchao.common.R
import org.cchao.common.expansion.clickWithTrigger
import org.cchao.common.ui.base.BaseDialog

/**
 * @author cchen6
 * @Date on 2020/1/14
 * @Description
 */
class PromptDialog : BaseDialog() {

    private var title: String = ""
    private var content: String = ""
    private var okStr: String? = null
    private var cancelStr: String? = null

    private var okListener: View.OnClickListener? = null
    private var cancelListener: View.OnClickListener? = null

    companion object {
        private const val KEY_TITLE = "key_title"
        private const val KEY_CONTENT = "key_content"
        private const val KEY_OK_STR = "key_ok_str"
        private const val KEY_CANCEL_STR = "key_cancel_str"

        fun show(fragmentManager: FragmentManager, title: String): PromptDialog {
            return show(fragmentManager, title, null, null, null)
        }

        fun show(fragmentManager: FragmentManager, title: String, okStr: String, cancelStr: String): PromptDialog {
            return show(fragmentManager, title, null, okStr, cancelStr)
        }

        fun show(fragmentManager: FragmentManager, title: String?, content: String?, okStr: String?, cancelStr: String?): PromptDialog {
            val promptDialog = PromptDialog()
            val bundle = Bundle()
            title!!.let { bundle.putString(KEY_TITLE, it) }
            content!!.let { bundle.putString(KEY_CONTENT, it) }
            okStr!!.let { bundle.putString(KEY_OK_STR, it) }
            cancelStr!!.let { bundle.putString(KEY_CANCEL_STR, it) }
            promptDialog.arguments = bundle
            promptDialog.show(fragmentManager, "PromptDialog" + System.currentTimeMillis())
            return promptDialog
        }
    }

    override fun bindLayout(): Int {
        return R.layout.dialog_prompt
    }

    override fun initData() {
        if (null != arguments) {
            okStr = arguments!!.getString(KEY_OK_STR, activity!!.getString(R.string.ok))
            cancelStr = arguments!!.getString(KEY_CANCEL_STR, activity!!.getString(R.string.cancel))
            title = arguments!!.getString(KEY_TITLE, "")
            content = arguments!!.getString(KEY_CONTENT, "")
        } else {
            okStr = activity!!.getString(R.string.ok)
            cancelStr = activity!!.getString(R.string.cancel)
        }
        dialog_prompt_title.text = title
        dialog_prompt_content.visibility = if (TextUtils.isEmpty(content)) View.GONE else View.VISIBLE
        dialog_prompt_content.text = content
        dialog_prompt_ok.text = okStr
        dialog_prompt_cancel.text = cancelStr
    }

    override fun bindEvent() {
        dialog_prompt_ok.clickWithTrigger(View.OnClickListener {
            if (null != okListener) {
                okListener!!.onClick(it)
            }
        })
        dialog_prompt_cancel.clickWithTrigger(View.OnClickListener {
            if (null != cancelListener) {
                cancelListener!!.onClick(it)
            }
        })
    }

    fun setOnOkClickListener(onClickListener: View.OnClickListener) {
        okListener = onClickListener
    }

    fun setOnCancelClickListener(onClickListener: View.OnClickListener) {
        cancelListener = onClickListener
    }
}