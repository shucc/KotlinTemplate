package org.cchao.kotlintemplate.expansion

import android.app.Activity
import org.cchao.common.utils.ScreenUtils
import org.cchao.kotlintemplate.ui.dialog.LoadingDialog

/**
 * 获取屏幕高度
 */
fun Activity.getScreenHeight(): Int {
    return ScreenUtils.height(this).px
}

/**
 * 获取屏幕宽度
 */
fun Activity.getScreenWidth(): Int {
    return ScreenUtils.width(this).px
}


fun Activity.showLoading() {
    showLoading(null)
}

fun Activity.showLoading(content: String?) {
    if (null == ExpansionObject.loadingDialog) {
        ExpansionObject.loadingDialog = LoadingDialog.show(this)
    }
    ExpansionObject.loadingDialog!!.setLoadingText(content)
    if (!ExpansionObject.loadingDialog!!.isShowing) {
        ExpansionObject.loadingDialog!!.show()
    }
}

fun Activity.hideLoading() {
    if (null == ExpansionObject.loadingDialog) {
        return
    }
    if (ExpansionObject.loadingDialog!!.isShowing) {
        ExpansionObject.loadingDialog!!.hide()
    }
}

fun Activity.clearLoading() {
    if (null != ExpansionObject.loadingDialog) {
        ExpansionObject.loadingDialog = null
    }
}