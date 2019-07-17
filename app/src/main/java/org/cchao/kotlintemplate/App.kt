package org.cchao.kotlintemplate

import android.app.Application
import android.content.Context

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class App : Application() {

    companion object {

        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
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
}