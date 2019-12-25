package org.cchao.kotlintemplate.model.body

import org.cchao.http.HttpRequestBody

class TestBody(val temp: String) : HttpRequestBody() {

    override fun getMethod(): Method {
        return Method.GET
    }

    override fun getUrl(): String {
        return "https://www.baidu.com"
    }
}