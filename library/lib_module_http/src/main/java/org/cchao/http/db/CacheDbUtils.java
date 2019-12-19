package org.cchao.http.db;

import org.cchao.common.IApplication;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class CacheDbUtils {

    private DaoSession daoSession;

    private static CacheDbUtils cacheDbUtils;

    private CacheDbUtils() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(IApplication.getInstance(), "cache.db", null);
        Database database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    public static CacheDbUtils getInstance() {
        if (null == cacheDbUtils) {
            cacheDbUtils = new CacheDbUtils();
        }
        return cacheDbUtils;
    }

    public long saveCache(CacheModel cacheModel) {
        return daoSession.getCacheModelDao().insert(cacheModel);
    }

    public void updateCache(CacheModel cacheModel) {
        daoSession.getCacheModelDao().update(cacheModel);
    }

    public void deleteCache(CacheModel cacheModel) {
        daoSession.getCacheModelDao().delete(cacheModel);
    }

    public CacheModel queryCacheModel(String key) {
        QueryBuilder<CacheModel> queryBuilder = daoSession.getCacheModelDao().queryBuilder();
        queryBuilder.where(CacheModelDao.Properties.Key.eq(key));
        List<CacheModel> cacheModelList = queryBuilder.list();
        return (null == cacheModelList || cacheModelList.isEmpty()) ? null : cacheModelList.get(0);
    }
}
