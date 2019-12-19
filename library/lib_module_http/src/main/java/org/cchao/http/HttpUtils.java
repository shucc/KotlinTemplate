package org.cchao.http;

import org.cchao.common.utils.FastJsonUtils;
import org.cchao.common.utils.GsonUtils;
import org.cchao.common.utils.Md5Utils;
import org.cchao.common.utils.NetworkUtils;
import org.cchao.http.api.Api;
import org.cchao.http.db.CacheDbUtils;
import org.cchao.http.db.CacheModel;
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

    public static Observable<HttpResponseModel<Object>> getBaseData(HttpRequestBody httpRequestBody) {
        return getBaseData(httpRequestBody, Object.class);
    }

    public static <T> Observable<T> getData(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getData(httpRequestBody, classType, false);
    }

    public static <T> Observable<HttpResponseModel<T>> getBaseData(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getBaseData(httpRequestBody, classType, false);
    }

    public static <T> Observable<T> getData(HttpRequestBody httpRequestBody, Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, classType, isCache);
    }

    public static <T> Observable<HttpResponseModel<T>> getBaseData(HttpRequestBody httpRequestBody, Class<T> classType, boolean isCache) {
        return processBaseData(httpRequestBody, classType, isCache);
    }

    private static <T> Observable<T> processData(final HttpRequestBody httpRequestBody, final Class<T> classType, final boolean isCache) {
        if (isCache && !NetworkUtils.isConnected()) {
            return getCacheData(httpRequestBody, classType, new ApiException(404, ""), isCache);
        }
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put("CONNECT_TIMEOUT", httpRequestBody.getConnectTime());
        headerMap.put("READ_TIMEOUT", httpRequestBody.getReadTime());
        headerMap.put("WRITE_TIMEOUT", httpRequestBody.getWriteTime());
        if (!httpRequestBody.getCustomHeader().isEmpty()) {
            headerMap.putAll(httpRequestBody.getCustomHeader());
        }
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
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                        return getCacheData(httpRequestBody, classType, throwable, isCache);
                    }
                });
        Observable<T> cacheObservable = null;
        if (isCache) {
            final CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(Md5Utils.getMd5(GsonUtils.toString(httpRequestBody)));
            if (null != cacheModel && ((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNetWorkTime()) {
                cacheObservable = Observable.just(GsonUtils.toObject(cacheModel.getContent(), classType))
                        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                            @Override
                            public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                                CacheDbUtils.getInstance().deleteCache(cacheModel);
                                return Observable.error(throwable);
                            }
                        });
            }
        }
        return null == cacheObservable ? netWorkObservable : Observable.concat(cacheObservable, netWorkObservable);
    }

    private static <T> Observable<HttpResponseModel<T>> processBaseData(HttpRequestBody httpRequestBody, Class<T> classType, boolean isCache) {
        return null;
    }

    private static <T> Observable<T> getCacheData(HttpRequestBody httpRequestBody, final Class<T> classType, Throwable throwable, boolean isCache) {
        if (!isCache) {
            return Observable.error(throwable);
        }
        String key = Md5Utils.getMd5(GsonUtils.toString(httpRequestBody));
        CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(key);
        if (null == cacheModel) {
            return Observable.error(throwable);
        }
        if (((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNetWorkTime()) {
            return Observable.just(GsonUtils.toObject(cacheModel.getContent(), classType));
        }
        CacheDbUtils.getInstance().deleteCache(cacheModel);
        return Observable.error(throwable);
    }

    private static <T> Observable<HttpResponseModel<T>> getCacheBaseData(HttpRequestBody httpRequestBody, Class<T> classType, Throwable throwable, boolean isCache) {
        if (!isCache) {
            return Observable.error(throwable);
        }
        String key = Md5Utils.getMd5(GsonUtils.toString(httpRequestBody));
        CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(key);
        if (null == cacheModel) {
            return Observable.error(throwable);
        }
        if (((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNetWorkTime()) {
            HttpResponseModel<T> responseModel = new HttpResponseModel<>();
            responseModel.setCode(cacheModel.getCode());
            responseModel.setMsg(cacheModel.getMsg());
            responseModel.setData(GsonUtils.toObject(cacheModel.getContent(), classType));
            return Observable.just(responseModel);
        }
        CacheDbUtils.getInstance().deleteCache(cacheModel);
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
