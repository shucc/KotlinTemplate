package org.cchao.kotlintemplate.expansion

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.view_toast.view.*
import org.cchao.common.utils.NetworkUtils
import org.cchao.common.utils.ScreenUtils
import org.cchao.kotlintemplate.R

/**
 * 吐司显示
 */
fun Context.showToast(content: String?) {
    if (TextUtils.isEmpty(content)) {
        return
    }
    if (System.currentTimeMillis() - ExpansionObject.showToastTime > 1000) {
        val view = LayoutInflater.from(this).inflate(R.layout.view_toast, null)
        view.txt_content.text = content
        val toast = Toast(this)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
    ExpansionObject.showToastTime = System.currentTimeMillis()
}

/**
 * 判断网络连接
 */
fun Context.isConnected(): Boolean {
    return NetworkUtils.isConnected()
}
