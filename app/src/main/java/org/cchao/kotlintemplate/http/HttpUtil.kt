import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.cchao.kotlintemplate.App
import org.cchao.kotlintemplate.exception.ApiException
import org.cchao.kotlintemplate.expansion.isConnected
import org.cchao.kotlintemplate.expansion.md5
import org.cchao.kotlintemplate.http.HttpRequestBody
import org.cchao.kotlintemplate.http.HttpResponseBody
import org.cchao.kotlintemplate.http.RetrofitUtil
import org.cchao.kotlintemplate.http.api.Api
import org.cchao.kotlintemplate.model.db.CacheModel
import org.cchao.kotlintemplate.model.db.CacheModelDao
import org.cchao.kotlintemplate.util.JsonUtil

object HttpUtil {

    private val TAG = "HttpUtils"

    private var api: Api? = null

    private fun getApi(): Api {
        if (api == null) {
            api = RetrofitUtil.create(Api::class.java)
        }
        return api!!
    }

    /**
     * 返回数据类型为T
     */
    fun <T> getData(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<T> {
        return getData(httpRequestBody, classType, false)
    }

    fun <T> getData(httpRequestBody: HttpRequestBody, classType: Class<T>, isCache: Boolean): Observable<T> {
        var observable: Observable<Any>
        when (isCache && !App.getInstance().isConnected()) {
            true -> {
                //TODO 没网络且需要使用缓存，在此处理
                observable = Observable.just(JsonUtil.toString(httpRequestBody))
                        .observeOn(Schedulers.io())
                        .flatMap {
                            val cacheModel = App.getInstance().getDaoSession().cacheModelDao.queryBuilder().where(CacheModelDao.Properties.Key.eq(it)).unique()
                            if (null == cacheModel) {
                                Observable.just("")
                            } else {
                                val response = JsonUtil.toObject(cacheModel.content, HttpResponseBody::class.java)
                                flatResponse(response)
                            }
                        }
            }
            false -> observable = getData(httpRequestBody, isCache)
        }
        return observable.map {
            val temp = JsonUtil.toString(it)
            if (TextUtils.isEmpty(temp)) null else JsonUtil.toObject(temp, classType)
        }
    }

    /**
     * 返回数据类型为Any，适用于不需使用返回数据网络请求
     */
    fun getData(httpRequestBody: HttpRequestBody): Observable<Any> {
        return getData(httpRequestBody, false)
    }

    private fun getData(httpRequestBody: HttpRequestBody, isCache: Boolean): Observable<Any> {
        return getApi().getData(httpRequestBody.url, httpRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext {
                    //TODO 需要保存缓存
                    if (isCache) {
                        val key = JsonUtil.toString(httpRequestBody).md5()
                        var cacheModel = App.getInstance().getDaoSession().cacheModelDao.queryBuilder().where(CacheModelDao.Properties.Key.eq(it)).unique()
                        if (null == cacheModel) {
                            cacheModel = CacheModel()
                            cacheModel.key = key
                            cacheModel.content = JsonUtil.toString(it)
                            App.getInstance().getDaoSession().cacheModelDao.insert(cacheModel)
                        } else {
                            cacheModel.content = JsonUtil.toString(it)
                            cacheModel.createDate = System.currentTimeMillis()
                            App.getInstance().getDaoSession().cacheModelDao.update(cacheModel)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { objectHttpResponseBody -> flatResponse(objectHttpResponseBody) }
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
    </T> */
    private fun <T> flatResponse(response: HttpResponseBody<T>): Observable<T> {
        return Observable.create { emitter ->
            if (response.isSuccess) {
                if (!emitter.isDisposed) {
                    emitter.onNext(response.data!!)
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