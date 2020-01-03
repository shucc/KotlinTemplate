package org.cchao.kotlintemplate

import org.cchao.common.IApplication
import org.cchao.kotlintemplate.model.db.DaoMaster
import org.cchao.kotlintemplate.model.db.DaoSession

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class App : IApplication() {

    private lateinit var daoSession: DaoSession

    companion object {

        lateinit var instance: IApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        instance.isDebug = BuildConfig.IS_DEBUG
        initDB()
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