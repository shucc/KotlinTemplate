package org.cchao.http.exception

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
class ApiException(code: Int, message: String) : Exception(message)
