package org.cchao.kotlintemplate.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import org.cchao.kotlintemplate.R

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
abstract class BaseDialog(context: Context) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
//        val dialogHeight = context.getScreenHeight() - context.getStatusHeight()
//        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, if (dialogHeight == 0) ViewGroup.LayoutParams.MATCH_PARENT else dialogHeight)
        initData()
        bindEvent()
    }

    abstract fun bindLayout(): Int

    abstract fun initData()

    abstract fun bindEvent()
}