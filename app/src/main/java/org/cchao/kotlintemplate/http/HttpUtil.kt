import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.cchao.kotlintemplate.exception.ApiException
import org.cchao.kotlintemplate.http.HttpRequestBody
import org.cchao.kotlintemplate.http.HttpResponseBody
import org.cchao.kotlintemplate.http.RetrofitUtil
import org.cchao.kotlintemplate.http.api.Api
import org.cchao.kotlintemplate.util.JsonUtil
import java.util.ArrayList

object HttpUtil {

    private val TAG = "HttpUtils"

    private var api: Api? = null

    private fun getApi(): Api? {
        if (api == null) {
            api = RetrofitUtil.create(Api::class.java)
        }
        return api
    }

    /**
     * 数据请求返回[]列表数据
     *
     * @param httpRequestBody
     * @param classType
     * @param <T>
     * @return
    </T> */
    fun <T> getDataList(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<List<T>> {
        return getData(httpRequestBody).map {
            val temp = JsonUtil.toString(httpRequestBody)
            if (TextUtils.isEmpty(temp)) {
                ArrayList()
            } else JsonUtil.toList(temp, classType)
        }
    }

    /**
     * @param httpRequestBody
     * @param classType
     * @param <T>
     * @return
    </T> */
    fun <T> getData(httpRequestBody: HttpRequestBody, classType: Class<T>): Observable<T> {
        return getData(httpRequestBody).map {
            val temp = JsonUtil.toString(httpRequestBody)
            if (TextUtils.isEmpty(temp)) {
                null
            } else JsonUtil.toObject(temp, classType)
        }
    }

    /**
     * 默认返回Object
     *
     * @param httpRequestBody
     * @return
     */
    fun getData(httpRequestBody: HttpRequestBody): Observable<Any> {
        return getApi()!!.getData(httpRequestBody.url, httpRequestBody)
                .subscribeOn(Schedulers.io())
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