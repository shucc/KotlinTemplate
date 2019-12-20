package org.cchao.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import org.cchao.common.IApplication;

public class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * 是否有网络连接
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) IApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        return null != cm && null != cm.getActiveNetworkInfo() && cm.getActiveNetworkInfo().isAvailable();
    }
}
