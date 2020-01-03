package org.cchao.http

import org.cchao.common.utils.JsonUtils
import org.cchao.common.utils.Md5Utils
import org.cchao.http.db.CacheDbUtils
import org.cchao.http.db.CacheModel

import io.reactivex.functions.Consumer

/**
 * @author cchen6
 * @Date on 2019/8/5
 * @Description
 */
internal class CacheConsumer(private val httpRequestBody: HttpRequestBody, private val isCache: Boolean) : Consumer<HttpResponseModel<Any>> {

    override fun accept(objectHttpResponseModel: HttpResponseModel<Any>?) {
        if (!isCache || null == objectHttpResponseModel || !objectHttpResponseModel.isSuccess) {
            return
        }
        val key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody))
        var cacheModel = CacheDbUtils.instance.queryCacheModel(key)
        if (null == cacheModel) {
            cacheModel = CacheModel(key, JsonUtils.toString(objectHttpResponseModel.data), objectHttpResponseModel.code, objectHttpResponseModel.msg, System.currentTimeMillis())
            CacheDbUtils.instance.saveCache(cacheModel)
        } else {
            cacheModel.setTime(System.currentTimeMillis())
            cacheModel.setContent(JsonUtils.toString(objectHttpResponseModel.data))
            cacheModel.setCode(objectHttpResponseModel.code)
            cacheModel.setMsg(objectHttpResponseModel.msg)
        }
    }
}
