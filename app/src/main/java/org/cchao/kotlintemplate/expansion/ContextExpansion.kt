package org.cchao.kotlintemplate.expansion

import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.view_toast.view.*
import org.cchao.common.utils.NetworkUtils
import org.cchao.kotlintemplate.R

/**
 * 吐司显示
 */
fun Context.showToast(content: String?) {
    if (TextUtils.isEmpty(content)) {
        return
    }
    if (System.currentTimeMillis() - ExpansionObject.showToastTime > 1000) {
        val view = LayoutInflater.from(this).inflate(R.layout.view_toast, null);
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

/**
 * 获取屏幕高度
 */
fun Context.getScreenHeight(): ScreenData {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return ScreenData(dm.heightPixels, dm.density)
}

/**
 * 获取屏幕宽度
 */
fun Context.getScreenWidth(): ScreenData {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return ScreenData(dm.widthPixels, dm.density)
}

/**
 * 获取顶部状态栏高度
 */
fun Context.getStatusHeight(): Int {
    var statusBarHeight = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}
