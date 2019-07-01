package org.cchao.kotlintemplate

import android.app.Application

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class App : Application() {

    companion object {
        private var instance: App? = null

        fun getInstance(): App {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}