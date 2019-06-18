package org.cchao.kotlintemplate.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import org.cchao.kotlintemplate.R
import org.cchao.kotlintemplate.expansion.getScreenHeight
import org.cchao.kotlintemplate.expansion.getStatusHeight

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
abstract class BaseDialog(context: Context) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
        val dialogHeight = context.getScreenHeight().px - context.getStatusHeight()
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, if (dialogHeight == 0) ViewGroup.LayoutParams.MATCH_PARENT else dialogHeight)
        initData()
        bindEvent()
    }

    abstract fun bindLayout(): Int

    abstract fun initData()

    abstract fun bindEvent()
}