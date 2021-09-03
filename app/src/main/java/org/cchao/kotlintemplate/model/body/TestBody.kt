package org.cchao.kotlintemplate.model.body

import org.cchao.http.HttpRequestBody

class TestBody : HttpRequestBody() {

    override fun getMethod(): Method {
        return Method.GET
    }

    override fun getUrl(): String {
        return "http://yapi.smaloo.com/mock/97/en/api/v1/c0/index/index"
    }
}