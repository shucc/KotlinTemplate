package org.cchao.kotlintemplate.exception

import java.lang.Exception

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class ApiException(code: Int, msg: String?) : Exception(msg)