package org.cchao.http.db

import org.cchao.common.IApplication

class CacheDbUtils private constructor() {

    private val daoSession: DaoSession

    companion object {

        private var cacheDbUtils: CacheDbUtils = CacheDbUtils()

        val instance: CacheDbUtils
            get() {
                return cacheDbUtils
            }
    }

    init {
        val helper = DaoMaster.DevOpenHelper(IApplication.instance, "cache.db", null)
        val database = helper.writableDb
        val daoMaster = DaoMaster(database)
        daoSession = daoMaster.newSession()
    }

    fun saveCache(cacheModel: CacheModel): Long {
        return daoSession.cacheModelDao.insert(cacheModel)
    }

    fun updateCache(cacheModel: CacheModel) {
        daoSession.cacheModelDao.update(cacheModel)
    }

    fun deleteCache(cacheModel: CacheModel) {
        daoSession.cacheModelDao.delete(cacheModel)
    }

    fun queryCacheModel(key: String): CacheModel? {
        val queryBuilder = daoSession.cacheModelDao.queryBuilder()
        queryBuilder.where(CacheModelDao.Properties.Key.eq(key))
        val cacheModelList = queryBuilder.list()
        return if (null == cacheModelList || cacheModelList.isEmpty()) null else cacheModelList[0]
    }

    fun clearAllCache() {
        daoSession.cacheModelDao.deleteAll()
    }
}
