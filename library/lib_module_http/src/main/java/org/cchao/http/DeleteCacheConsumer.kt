package org.cchao.http

import io.reactivex.rxjava3.functions.Consumer
import org.cchao.common.utils.JsonUtils
import org.cchao.common.utils.Md5Utils
import org.cchao.http.db.CacheDbUtils

internal class DeleteCacheConsumer(private val httpRequestBody: HttpRequestBody, private val isCache: Boolean) : Consumer<Throwable> {

    override fun accept(throwable: Throwable) {
        if (isCache) {
            val key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody)).plus(httpRequestBody.url)
            val cacheModel = CacheDbUtils.instance.queryCacheModel(key)
            if (null != cacheModel) {
                CacheDbUtils.instance.deleteCache(cacheModel)
            }
        }
    }
}
