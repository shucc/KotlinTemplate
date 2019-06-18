package org.cchao.kotlintemplate.http.api

import io.reactivex.Observable
import org.cchao.kotlintemplate.http.HttpRequestBody
import org.cchao.kotlintemplate.http.HttpResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
interface Api {

    @POST
    fun getData(@Url url: String, @Body body: HttpRequestBody): Observable<HttpResponseBody<Any>>
}