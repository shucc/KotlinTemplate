package org.cchao.common

import android.app.Application
import android.content.Context

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
open class IApplication : Application() {

    var isDebug = true

    companion object {

        lateinit var instance: IApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initStetho()
    }

    private fun initStetho() {
        if (!isDebug) {
            return
        }
        try {
            val cls = Class.forName("com.facebook.stetho.Stetho")
            val method = cls.getDeclaredMethod("initializeWithDefaults", Context::class.java)
            method.isAccessible = true
            method.invoke(null, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
