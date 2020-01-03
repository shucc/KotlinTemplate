package org.cchao.common.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager

import org.cchao.common.IApplication

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
object ScreenUtils {

    //打开关闭弹出框
    val OPEN_POP = 0
    val HIDE_POP = 1

    /**
     * 获取顶部状态栏高度
     *
     * @return
     */
    val statusBarHeight: Int
        get() {
            var statusBarHeight = 0
            val resourceId = IApplication.instance.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = IApplication.instance.resources.getDimensionPixelSize(resourceId)
            }
            return statusBarHeight
        }

    /**
     * 获取屏幕
     */
    private fun getDisplay(activity: Activity): Display {
        return activity.windowManager.defaultDisplay
    }

    fun dipToPx(dpValue: Float): Int {
        val scale = IApplication.instance.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 屏幕宽度
     */
    fun width(activity: Activity): Data {
        val dm = DisplayMetrics()
        getDisplay(activity).getMetrics(dm)
        return Data(dm.widthPixels, dm.density)
    }

    /**
     * 屏幕高度
     */
    fun height(activity: Activity): Data {
        val dm = DisplayMetrics()
        getDisplay(activity).getMetrics(dm)
        return Data(dm.heightPixels, dm.density)
    }

    /**
     * 设置全屏与取消全屏
     *
     * @param activity
     * @param fullscreen
     */
    fun setFullscreen(activity: Activity, fullscreen: Boolean) {
        val attrs = activity.window.attributes
        if (fullscreen) {
            attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN.inv()
        } else {
            attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        }
        activity.window.attributes = attrs
    }

    /**
     * 设置弹出框出现与隐藏时背景透明度变化
     *
     * @param type
     */
    fun setWindowAlpha(activity: Activity, type: Int) {
        //设置背景颜色变暗
        val lp = activity.window.attributes
        if (type == OPEN_POP) {
            lp.alpha = 0.6f
        } else {
            lp.alpha = 1.0f
        }
        activity.window.attributes = lp
    }

    class Data(var px: Int, density: Float) {

        var dp: Float = 0.toFloat()

        init {
            this.dp = px / density
        }
    }
}
