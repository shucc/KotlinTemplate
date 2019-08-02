package org.cchao.http;

import org.cchao.http.api.Api;
import org.cchao.http.exception.ApiException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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
        return null;
    }

    private static <T> Observable<T> processData(HttpRequestBody httpRequestBody, Class<T> classType, boolean isCache) {
        return null;
    }

    private static <T> Observable<T> getCacheData(HttpRequestBody httpRequestBody, Class<T> classType, Throwable throwable, boolean isCache) {
        if (!isCache) {
            return Observable.error(throwable);
        }
        return null;
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
