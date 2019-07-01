package org.cchao.kotlintemplate.http

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class HttpResponseBody<T> {

    var msg: String? = null

    var code: Int = 0

    var data: T? = null

    val isSuccess: Boolean
        get() = 0 == code
}