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
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    public static <T> Observable<T> getData(final HttpRequestBody httpRequestBody, final Class<T> classType, final boolean isCache) {
        return processData(httpRequestBody, isCache)
                .flatMap(objectHttpResponseModel -> flatResponse(objectHttpResponseModel))
                .map(o -> JsonUtils.fromJson(String.valueOf(o), classType))
                .doOnError(throwable -> {
                    if (isCache) {
                        deleteCache(httpRequestBody);
                    }
                });
    }

    public static <T> Observable<List<T>> getDataList(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .flatMap(objectHttpResponseModel -> flatResponse(objectHttpResponseModel))
                .map(o -> JsonUtils.toList(String.valueOf(o), classType))
                .doOnError(throwable -> {
                    if (isCache) {
                        deleteCache(httpRequestBody);
                    }
                });
    }

    public static <T> Observable<HttpResponseModel<T>> getBaseData(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(objectHttpResponseModel -> {
                    HttpResponseModel<T> responseModel = new HttpResponseModel<>();
                    responseModel.setMsg(objectHttpResponseModel.getMsg());
                    responseModel.setCode(objectHttpResponseModel.getCode());
                    responseModel.setData(JsonUtils.fromJson(String.valueOf(objectHttpResponseModel.getData()), classType));
                    return responseModel;
                })
                .doOnError(throwable -> {
                    if (isCache) {
                        deleteCache(httpRequestBody);
                    }
                });
    }

    public static <T> Observable<HttpResponseModel<List<T>>> getBaseDataList(final HttpRequestBody httpRequestBody, final Class<T> classType, boolean isCache) {
        return processData(httpRequestBody, isCache)
                .map(objectHttpResponseModel -> {
                    HttpResponseModel<List<T>> responseModel = new HttpResponseModel<>();
                    responseModel.setMsg(objectHttpResponseModel.getMsg());
                    responseModel.setCode(objectHttpResponseModel.getCode());
                    responseModel.setData(JsonUtils.toList(JsonUtils.toString(objectHttpResponseModel.getData()), classType));
                    return responseModel;
                })
                .doOnError(throwable -> {
                    if (isCache) {
                        deleteCache(httpRequestBody);
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
            HttpResponseModel<Object> objectHttpResponseModel = getCacheResponse(httpRequestBody, new ApiException(404, ""), isCache);
            if (null == objectHttpResponseModel.getData()) {
                return Observable.error(new ApiException(HttpResponseModel.CODE_ERROR, ""));
            }
            return Observable.just(objectHttpResponseModel);
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
                .onErrorResumeNext(throwable -> {
                    HttpResponseModel<Object> objectHttpResponseModel = getCacheResponse(httpRequestBody, throwable, isCache);
                    if (null == objectHttpResponseModel.getData()) {
                        return Observable.error(new ApiException(HttpResponseModel.CODE_ERROR, throwable.getMessage()));
                    }
                    return Observable.just(objectHttpResponseModel);
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
        if ((NetworkUtils.isConnected() && ((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNetWorkTime())
                || (!NetworkUtils.isConnected() && ((System.currentTimeMillis() - cacheModel.getTime()) / 1000) < httpRequestBody.getCookieNoNetWorkTime())) {
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
        return Observable.create(emitter -> {
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
        });
    }
}
