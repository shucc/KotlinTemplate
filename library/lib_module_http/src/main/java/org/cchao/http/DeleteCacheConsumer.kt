package org.cchao.http

import org.cchao.common.utils.JsonUtils
import org.cchao.common.utils.Md5Utils
import org.cchao.http.db.CacheDbUtils
import org.cchao.http.db.CacheModel

import io.reactivex.functions.Consumer

internal class DeleteCacheConsumer(private val httpRequestBody: HttpRequestBody, private val isCache: Boolean) : Consumer<Throwable> {

    @Throws(Exception::class)
    override fun accept(throwable: Throwable) {
        if (isCache) {
            val key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody))
            val cacheModel = CacheDbUtils.instance.queryCacheModel(key)
            if (null != cacheModel) {
                CacheDbUtils.instance.deleteCache(cacheModel)
            }
        }
    }
}
