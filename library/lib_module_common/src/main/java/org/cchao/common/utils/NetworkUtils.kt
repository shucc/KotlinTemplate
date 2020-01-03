package org.cchao.common.utils

import android.content.Context
import android.net.ConnectivityManager

import org.cchao.common.IApplication

object NetworkUtils {

    /**
     * 是否有网络连接
     *
     * @return
     */
    val isConnected: Boolean
        get() {
            val cm = IApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return null != cm.activeNetworkInfo && cm.activeNetworkInfo!!.isAvailable
        }
}
