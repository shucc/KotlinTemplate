package org.cchao.http;

import org.cchao.common.utils.FastJsonUtils;
import org.cchao.common.utils.JsonUtils;
import org.cchao.common.utils.Md5Utils;
import org.cchao.common.utils.NetworkUtils;
import org.cchao.http.api.Api;
import org.cchao.http.db.CacheDbUtils;
import org.cchao.http.db.CacheModel;
import org.cchao.http.exception.ApiException;
import org.cchao.http.exception.RetryWhenNetworkException;

import java.util.HashMap;
import java.util.List;

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

    public static <T> Observable<List<T>> getDataList(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getDataList(httpRequestBody, classType, false);
    }

    public static <T> Observable<HttpResponseModel<T>> getBaseData(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getBaseData(httpRequestBody, classType, false);
    }

    public static <T> Observable<HttpResponseModel<List<T>>> getBaseDataList(HttpRequestBody httpRequestBody, Class<T> classType) {
        return getBaseDataList(httpRequestBody, classType, false);
    }

    public static <T> Observable<T> getData(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(new Function<HttpResponseModel<Object>, T>() {
                    @Override
                    public T apply(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
                        return JsonUtils.fromJson(String.valueOf(objectHttpResponseModel.getData()), classType);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(Throwable throwable) throws Exception {
                        deleteCache(httpRequestBody);
                        return Observable.error(throwable);
                    }
                });
    }

    public static <T> Observable<List<T>> getDataList(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(new Function<HttpResponseModel<Object>, List<T>>() {
                    @Override
                    public List<T> apply(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
                        return JsonUtils.toList(String.valueOf(objectHttpResponseModel.getData()), classType);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends List<T>>>() {
                    @Override
                    public ObservableSource<? extends List<T>> apply(Throwable throwable) throws Exception {
                        deleteCache(httpRequestBody);
                        return Observable.error(throwable);
                    }
                });
    }

    public static <T> Observable<HttpResponseModel<T>> getBaseData(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(new Function<HttpResponseModel<Object>, HttpResponseModel<T>>() {
                    @Override
                    public HttpResponseModel<T> apply(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
                        HttpResponseModel<T> responseModel = new HttpResponseModel<>();
                        responseModel.setMsg(objectHttpResponseModel.getMsg());
                        responseModel.setCode(objectHttpResponseModel.getCode());
                        responseModel.setData(JsonUtils.fromJson(String.valueOf(objectHttpResponseModel.getData()), classType));
                        return responseModel;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends HttpResponseModel<T>>>() {
                    @Override
                    public ObservableSource<? extends HttpResponseModel<T>> apply(Throwable throwable) throws Exception {
                        deleteCache(httpRequestBody);
                        return Observable.error(throwable);
                    }
                });
    }

    public static <T> Observable<HttpResponseModel<List<T>>> getBaseDataList(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(new Function<HttpResponseModel<Object>, HttpResponseModel<List<T>>>() {
                    @Override
                    public HttpResponseModel<List<T>> apply(HttpResponseModel<Object> objectHttpResponseModel) throws Exception {
                        HttpResponseModel<List<T>> responseModel = new HttpResponseModel<>();
                        responseModel.setMsg(objectHttpResponseModel.getMsg());
                        responseModel.setCode(objectHttpResponseModel.getCode());
                        responseModel.setData(JsonUtils.toList(JsonUtils.toString(objectHttpResponseModel.getData()), classType));
                        return responseModel;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends HttpResponseModel<List<T>>>>() {
                    @Override
                    public ObservableSource<? extends HttpResponseModel<List<T>>> apply(Throwable throwable) throws Exception {
                        deleteCache(httpRequestBody);
                        return Observable.error(throwable);
                    }
                });
    }

    private static void deleteCache(HttpRequestBody httpRequestBody) {
        String key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody));
        CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(key);
        if (null != cacheModel) {
            CacheDbUtils.getInstance().deleteCache(cacheModel);
        }
    }

    private static Observable<HttpResponseModel<Object>> processData(final HttpRequestBody httpRequestBody, final boolean isCache) {
        if (isCache && !NetworkUtils.isConnected()) {
            return Observable.just(getCacheResponse(httpRequestBody, new ApiException(404, ""), isCache));
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
                basicObservable = getApi().postData(httpRequestBody.getUrl(), headerMap, FastJsonUtils.toMap(JsonUtils.toString(httpRequestBody)));
                break;
        }
        Observable<HttpResponseModel<Object>> netWorkObservable = basicObservable
                .retryWhen(new RetryWhenNetworkException(httpRequestBody.getRetryCount(), httpRequestBody.getRetryDelay(), httpRequestBody.getRetryIncreaseDelay()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new CacheConsumer(httpRequestBody, isCache))
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends HttpResponseModel<Object>>>() {
                    @Override
                    public ObservableSource<? extends HttpResponseModel<Object>> apply(Throwable throwable) throws Exception {
                        return Observable.just(getCacheResponse(httpRequestBody, throwable, isCache));
                    }
                });
        Observable<HttpResponseModel<Object>> cacheObservable = null;
        if (isCache) {
            HttpResponseModel<Object> cacheData = getCacheResponse(httpRequestBody, new Throwable(""), isCache);
            if (cacheData.getData() != null) {
                cacheObservable = Observable.just(cacheData);
            }
        }
        return null == cacheObservable ? netWorkObservable : Observable.concat(cacheObservable, netWorkObservable);
    }

    private static HttpResponseModel<Object> getCacheResponse(HttpRequestBody httpRequestBody, Throwable throwable, boolean isCache) {
        HttpResponseModel<Object> responseModel = new HttpResponseModel<>();
        if (!isCache) {
            responseModel.setMsg(throwable.getMessage());
            return responseModel;
        }
        String key = Md5Utils.getMd5(JsonUtils.toString(httpRequestBody));
        CacheModel cacheModel = CacheDbUtils.getInstance().queryCacheModel(key);
        if (null == cacheModel) {
            responseModel.setMsg(throwable.getMessage());
            return responseModel;
        }
        if (((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNetWorkTime()) {
            responseModel.setCode(cacheModel.getCode());
            responseModel.setMsg(cacheModel.getMsg());
            responseModel.setData(cacheModel.getContent());
            return responseModel;
        }
        CacheDbUtils.getInstance().deleteCache(cacheModel);
        responseModel.setMsg(throwable.getMessage());
        return responseModel;
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
