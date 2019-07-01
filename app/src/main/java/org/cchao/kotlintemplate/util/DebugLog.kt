package org.cchao.kotlintemplate.util

import android.util.Log
import org.cchao.kotlintemplate.BuildConfig

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
object DebugLog {

    fun d(tag: String, content: String) {
        if (!BuildConfig.DEBUG) {
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
                Log.d(tag, content.substring(i))
            }
            i += 4000
        }
    }
}