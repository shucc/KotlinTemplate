package org.cchao.http

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import org.cchao.common.IApplication
import org.cchao.common.utils.FileUtils
import org.cchao.common.utils.JsonUtils
import org.cchao.common.utils.Md5Utils
import org.cchao.common.utils.NetworkUtils
import org.cchao.http.api.Api
import org.cchao.http.db.CacheDbUtils
import org.cchao.http.exception.ApiException
import org.cchao.http.exception.RetryWhenNetworkException
import java.util.*

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
object HttpUtils {

    private var api: Api? = null

    private fun getApi(): Api {
        if (null == api) {
            api = RetrofitUtils.create(Api::class.java)
        }
        return api!!
    }

    fun downloadFile(downloadUrl: String, fileName: String): Observable<String?> {
        return getApi().download(downloadUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap {
                    Observable.just(FileUtils.writeResponseBodyToDisk(fileName, it))
                }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getData(httpRequestBody: HttpRequestBody): Observable<Any> {
        return getData(httpRequestBody, Any::class.java)
    }

    fun getBaseData(httpRequestBody: HttpRequestBody): Observable<HttpResponseModel<Any>> {
        return getBaseData(httpRequestBody, Any::class.java)
    }

    fun <T> getData(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<T> {
        return getData(httpRequestBody, classType, false)
    }

    fun <T> getDataList(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<List<T>> {
        return getDataList(httpRequestBody, classType, false)
    }

    fun <T> getBaseData(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<HttpResponseModel<T>> {
        return getBaseData(httpRequestBody, classType, false)
    }

    fun <T> getBaseDataList(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<HttpResponseModel<List<T>>> {
        return getBaseDataList(httpRequestBody, classType, false)
    }

    fun <T> getData(httpRequestBody: HttpRequestBody, classType: Class<T>, isCache: Boolean): Observable<T> {
        return processData(httpRequestBody, isCache)
                .flatMap { flatResponse(it) }
                .map { o -> JsonUtils.fromJson(JsonUtils.toString(o), classType) }
                .doOnError(DeleteCacheConsumer(httpRequestBody, isCache))
    }

    fun <T> getDataList(httpRequestBody: HttpRequestBody, classType: Class<T>, isCache: Boolean): Observable<List<T>> {
        return processData(httpRequestBody, isCache)
                .flatMap { flatResponse(it) }
                .map { o -> JsonUtils.toList(JsonUtils.toString(o), classType) }
                .doOnError(DeleteCacheConsumer(httpRequestBody, isCache))
    }

    fun <T> getBaseData(httpRequestBody: HttpRequestBody, classType: Class<T>, isCache: Boolean): Observable<HttpResponseModel<T>> {
        return processData(httpRequestBody, isCache)
                .map { objectHttpResponseModel ->
                    val responseModel = HttpResponseModel<T>()
                    responseModel.msg = objectHttpResponseModel.msg
                    responseModel.code = objectHttpResponseModel.code
                    responseModel.data = JsonUtils.fromJson(JsonUtils.toString(objectHttpResponseModel.data), classType)
                    responseModel
                }
                .doOnError(DeleteCacheConsumer(httpRequestBody, isCache))
    }

    fun <T> getBaseDataList(httpRequestBody: HttpRequestBody, classType: Class<T>, isCache: Boolean): Observable<HttpResponseModel<List<T>>> {
        return processData(httpRequestBody, isCache)
                .map { objectHttpResponseModel ->
                    val responseModel = HttpResponseModel<List<T>>()
                    responseModel.msg = objectHttpResponseModel.msg
                    responseModel.code = objectHttpResponseModel.code
                    responseModel.data = JsonUtils.toList(JsonUtils.toString(objectHttpResponseModel.data), classType)
                    responseModel
                }
                .doOnError(DeleteCacheConsumer(httpRequestBody, isCache))
    }

    private fun processData(httpRequestBody: HttpRequestBody, isCache: Boolean): Observable<HttpResponseModel<Any>> {
        //无网络处理
        if (!NetworkUtils.isConnected) {
            val toastStr = IApplication.instance.resources.getString(R.string.no_internet_connection)
            if (!isCache) {
                return Observable.error(ApiException(HttpResponseModel.CODE_ERROR, toastStr))
            }
            val objectHttpResponseModel = getCacheResponse(httpRequestBody, ApiException(HttpResponseModel.CODE_ERROR, toastStr), isCache)
            return if (null == objectHttpResponseModel.data) {
                Observable.error(ApiException(HttpResponseModel.CODE_ERROR, toastStr))
            } else Observable.just(objectHttpResponseModel)
        }
        val headerMap = HashMap<String, Any>()
        headerMap[RetrofitUtils.CONNECT_TIMEOUT] = httpRequestBody.connectTime
        headerMap[RetrofitUtils.READ_TIMEOUT] = httpRequestBody.readTime
        headerMap[RetrofitUtils.WRITE_TIMEOUT] = httpRequestBody.writeTime
        if (!httpRequestBody.customHeader.isNullOrEmpty()) {
            headerMap.putAll(httpRequestBody.customHeader)
        }
        val basicObservable: Observable<HttpResponseModel<Any>> = when (httpRequestBody.method) {
            HttpRequestBody.Method.GET -> getApi().getData(httpRequestBody.url, headerMap, JsonUtils.toMap(JsonUtils.toString(httpRequestBody)))
            HttpRequestBody.Method.POST -> getApi().postData(httpRequestBody.url, headerMap, JsonUtils.toMap(JsonUtils.toString(httpRequestBody)))
            HttpRequestBody.Method.POST_BODY -> getApi().postBodyData(httpRequestBody.url, headerMap, httpRequestBody)
            else -> throw AssertionError("Request body method is wrong!")
        }
        val netWorkObservable = basicObservable
                .retryWhen(RetryWhenNetworkException(httpRequestBody.retryCount!!, httpRequestBody.retryDelay!!, httpRequestBody.retryIncreaseDelay!!))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(CacheConsumer(httpRequestBody, isCache))
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Function {
                    val response = getCacheResponse(httpRequestBody, it, isCache)
                    if (null != response.data) {
                        Observable.just(response)
                    } else {
                        Observable.error(ApiException(HttpResponseModel.CODE_ERROR, response.msg))
                    }
                })
        var cacheObservable: Observable<HttpResponseModel<Any>>? = null
        if (isCache) {
            val cacheData = getCacheResponse(httpRequestBody, Throwable(""), isCache)
            if (cacheData.data != null) {
                cacheObservable = Observable.just(cacheData)
            }
        }
        return if (null == cacheObservable) netWorkObservable else Observable.concat(cacheObservable, netWorkObservable)
    }

    private fun getCacheResponse(httpRequestBody: HttpRequestBody, throwable: Throwable, isCache: Boolean): HttpResponseModel<Any> {
        val responseModel = HttpResponseModel<Any>()
        if (!isCache) {
            responseModel.msg = throwable.message
            return responseModel
        }
        val key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody)).plus(httpRequestBody.url)
        val cacheModel = CacheDbUtils.instance.queryCacheModel(key)
        if (null == cacheModel) {
            responseModel.msg = throwable.message
            return responseModel
        }
        if (NetworkUtils.isConnected && (System.currentTimeMillis() - cacheModel.time!!) / 1000 < httpRequestBody.cookieNetWorkTime || !NetworkUtils.isConnected && (System.currentTimeMillis() - cacheModel.time!!) / 1000 < httpRequestBody.cookieNoNetWorkTime) {
            responseModel.code = cacheModel.code
            responseModel.msg = cacheModel.msg
            responseModel.data = cacheModel.content
            return responseModel
        }
        CacheDbUtils.instance.deleteCache(cacheModel)
        responseModel.msg = throwable.message
        return responseModel
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
    </T> */
    private fun <T> flatResponse(response: HttpResponseModel<T>): Observable<T> {
        return Observable.create { emitter ->
            if (response.isSuccess) {
                if (!emitter.isDisposed) {
                    emitter.onNext(response.data)
                }
            } else {
                if (!emitter.isDisposed) {
                    emitter.onError(ApiException(response.code, response.msg))
                }
            }
            if (!emitter.isDisposed) {
                emitter.onComplete()
            }
        }
    }
}
