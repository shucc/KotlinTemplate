package org.cchao.http

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import okio.BufferedSource
import org.cchao.common.IApplication
import org.cchao.common.utils.L
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
internal class RetrofitUtils private constructor() {

    init {
        throw AssertionError("No instances!")
    }

    companion object {

        private val RETROFIT: Retrofit
        const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "READ_TIMEOUT"
        const val WRITE_TIMEOUT = "WRITE_TIMEOUT"

        init {

            val okHttpBuild = OkHttpClient.Builder()

            okHttpBuild.interceptors().add(Interceptor {
                var request = it.request()
                var connectTimeout = it.connectTimeoutMillis()
                var readTimeout = it.readTimeoutMillis()
                var writeTimeout = it.writeTimeoutMillis()
                val connectTimeoutNew = request.header(CONNECT_TIMEOUT)
                val readTimeoutNew = request.header(READ_TIMEOUT)
                val writeTimeoutNew = request.header(WRITE_TIMEOUT)
                if (!TextUtils.isEmpty(connectTimeoutNew)) {
                    connectTimeout = Integer.valueOf(connectTimeoutNew!!)
                }
                if (!TextUtils.isEmpty(readTimeoutNew)) {
                    readTimeout = Integer.valueOf(readTimeoutNew!!)
                }
                if (!TextUtils.isEmpty(writeTimeoutNew)) {
                    writeTimeout = Integer.valueOf(writeTimeoutNew!!)
                }
                request = it.request()
                        .newBuilder()
                        .removeHeader(CONNECT_TIMEOUT)
                        .removeHeader(WRITE_TIMEOUT)
                        .removeHeader(READ_TIMEOUT)
                        .build()
                val response = it
                        .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                        .proceed(request)
                if (IApplication.instance.isDebug) {
                    val buffer = Buffer()
                    var source: BufferedSource? = null
                    val responseBody = response.body()
                    if (null != request.body() && null != responseBody) {
                        request.body()!!.writeTo(buffer)
                        source = responseBody.source()
                        source!!.request(Integer.MAX_VALUE.toLong())
                    }
                    L.d("Retrofit", "=========================================================================================")
                    L.d("Retrofit", request.method() + "-->" + request.url())
                    L.d("Retrofit", buffer.readUtf8())
                    if (null != source) {
                        L.d("Retrofit", source.buffer().clone().readUtf8())
                    }
                    L.d("Retrofit", "========================================================================================")
                }
                response
            })
            if (IApplication.instance.isDebug) {
                try {
                    val cls = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor")
                    okHttpBuild.addNetworkInterceptor(cls.newInstance() as Interceptor)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val client = okHttpBuild.readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()

            RETROFIT = Retrofit.Builder()
                    .client(client)
                    .baseUrl("https://www.baidu.com/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
        }

        fun <T> create(service: Class<T>): T {
            return RETROFIT.create(service)
        }
    }
}
