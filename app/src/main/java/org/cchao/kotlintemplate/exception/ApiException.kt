package org.cchao.kotlintemplate.exception

import java.lang.Exception

/**
 * @author cchen6
 * @Date on ${Date}
 * @Description
 */
class ApiException(code: Int, msg: String?) : Exception(msg)