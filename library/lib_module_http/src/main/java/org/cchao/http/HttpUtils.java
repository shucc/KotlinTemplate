package org.cchao.http;

import org.cchao.common.utils.FastJsonUtils;
import org.cchao.common.utils.GsonUtils;
import org.cchao.common.utils.Md5Utils;
import org.cchao.http.api.Api;
import org.cchao.http.exception.ApiException;
import org.cchao.http.exception.RetryWhenNetworkException;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class HttpUtils {

    private static Api api;

    private HttpUtils() {

    }

    private static Api getApi() {
        if (null == api) {
            api = RetrofitUtils.create(Api.class);
        }
        return api;
    }

    public static Observable<Object> getData(HttpRequestBody httpRequestBody) {
        return getData(httpRequestBody, Object.class);
    }

    public static <T> Observable<T> getData(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getData(httpRequestBody, classType, false);
    }

    public static <T> Observable<T> getData(HttpRequestBody httpRequestBody, Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, classType, isCache);
    }

    private static <T> Observable<T> processData(HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        HashMap<String, Long> headerMap = new HashMap<>();
        headerMap.put("CONNECT_TIMEOUT", httpRequestBody.getConnectTime());
        headerMap.put("READ_TIMEOUT", httpRequestBody.getReadTime());
        headerMap.put("WRITE_TIMEOUT", httpRequestBody.getWriteTime());
        Observable<HttpResponseModel<Object>> basicObservable;
        switch (httpRequestBody.getMethod()) {
            case GET:
                basicObservable = getApi().getData(httpRequestBody.getUrl(), headerMap);
                break;
            case POSTBODY:
                basicObservable = getApi().postBodyData(httpRequestBody.getUrl(), headerMap, httpRequestBody);
                break;
            default:
                basicObservable = getApi().postData(httpRequestBody.getUrl(), headerMap, FastJsonUtils.toMap(GsonUtils.toString(httpRequestBody)));
                break;
        }
        Observable<T> netWorkObservable = basicObservable
                .retryWhen(new RetryWhenNetworkException(httpRequestBody.getRetryCount(), httpRequestBody.getRetryDelay(), httpRequestBody.getRetryIncreaseDelay()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new CacheConsumer(httpRequestBody, isCache))
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<HttpResponseModel<Object>, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
                        return flatResponse(objectHttpResponseModel);
                    }
                })
                .map(new Function<Object, T>() {
                    @Override
                    public T apply(Object o) throws Exception {
                        return GsonUtils.toObject(GsonUtils.toString(o), classType);
                    }
                });
        return netWorkObservable;
    }

    private static <T> Observable<T> getCacheData(HttpRequestBody httpRequestBody, final Class<T> classType, Throwable throwable, boolean isCache) {
        if (!isCache) {
            return Observable.error(throwable);
        }
        String key = Md5Utils.getMd5(GsonUtils.toString(httpRequestBody));
        return Observable.error(throwable);
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    private static <T> Observable<T> flatResponse(final HttpResponseModel<T> response) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                if (response.isSuccess()) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(response.getData());
                    }
                } else {
                    if (!emitter.isDisposed()) {
                        emitter.onError(new ApiException(response.getCode(), response.getMsg()));
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }
}
