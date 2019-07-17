package org.cchao.kotlintemplate

import android.app.Application
import android.content.Context
import org.cchao.kotlintemplate.model.db.DaoMaster
import org.cchao.kotlintemplate.model.db.DaoSession
import java.lang.Exception

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class App : Application() {

    private lateinit var daoSession: DaoSession

    companion object {

        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initStetho()
        initDB()
    }

    private fun initStetho() {
        if (!BuildConfig.DEBUG) {
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

    private fun initDB() {
        val helper = DaoMaster.DevOpenHelper(this, "temp.db", null)
        val db = helper.writableDb
        val dbMaster = DaoMaster(db)
        daoSession = dbMaster.newSession()
    }

    fun getDaoSession(): DaoSession {
        return daoSession
    }
}