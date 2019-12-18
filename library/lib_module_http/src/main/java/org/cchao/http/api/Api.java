package org.cchao.http.api;

import org.cchao.http.HttpRequestBody;
import org.cchao.http.HttpResponseModel;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public interface Api {

    @FormUrlEncoded
    @POST
    Observable<HttpResponseModel<Object>> postData(@Url String url, @HeaderMap HashMap<String, Long> headMap, @FieldMap Map<String, Object> fieldMap);

    @POST
    Observable<HttpResponseModel<Object>> postBodyData(@Url String url, @HeaderMap HashMap<String, Long> headMap, @Body HttpRequestBody httpRequestBody);

    @GET
    Observable<HttpResponseModel<Object>> getData(@Url String url, @HeaderMap HashMap<String, Long> headMap);
}
