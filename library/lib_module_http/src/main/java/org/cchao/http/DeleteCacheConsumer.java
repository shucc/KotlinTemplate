package org.cchao.http;

import org.cchao.common.utils.JsonUtils;
import org.cchao.common.utils.Md5Utils;
import org.cchao.http.db.CacheDbUtils;
import org.cchao.http.db.CacheModel;

import io.reactivex.functions.Consumer;

class DeleteCacheConsumer implements Consumer<Throwable> {

    private HttpRequestBody httpRequestBody;

    private boolean isCache;

    DeleteCacheConsumer(HttpRequestBody httpRequestBody, boolean isCache) {
        this.httpRequestBody = httpRequestBody;
        this.isCache = isCache;
    }

    @Override
    public void accept(Throwable throwable) throws Exception {
        if (isCache) {
            String key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody));
            CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(key);
            if (null != cacheModel) {
                CacheDbUtils.getInstance().deleteCache(cacheModel);
            }
        }
    }
}
