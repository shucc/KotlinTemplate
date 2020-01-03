package org.cchao.common.utils

import android.text.TextUtils
import android.util.Log

import org.cchao.common.IApplication

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
object L {

    private val TAG = "DebugLog"

    fun d(content: String) {
        d(TAG, content)
    }

    fun d(tag: String, content: String) {
        if (!IApplication.instance.isDebug) {
            return
        }
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(content)) {
            return
        }
        if (content.length <= 4000) {
            Log.d(tag, content)
            return
        }
        var i = 0
        while (i < content.length) {
            if (i + 4000 < content.length) {
                Log.d(tag, content.substring(i, i + 4000))
            } else {
                Log.d(tag, content.substring(i, content.length))
            }
            i += 4000
        }
    }

    fun e(content: String) {
        e(TAG, content)
    }

    fun e(tag: String, content: String) {
        if (!IApplication.instance.isDebug) {
            return
        }
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(content)) {
            return
        }
        if (content.length <= 4000) {
            Log.e(tag, content)
            return
        }
        var i = 0
        while (i < content.length) {
            if (i + 4000 < content.length) {
                Log.e(tag, content.substring(i, i + 4000))
            } else {
                Log.e(tag, content.substring(i, content.length))
            }
            i += 4000
        }
    }
}
