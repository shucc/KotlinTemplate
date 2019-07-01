package org.cchao.kotlintemplate.http.body

import org.cchao.kotlintemplate.Constant
import org.cchao.kotlintemplate.http.HttpRequestBody

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
class TestBody : HttpRequestBody() {

    override val url: String
        get() = Constant.BASE_URL + "baidu"
}