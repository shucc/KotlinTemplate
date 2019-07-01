package org.cchao.kotlintemplate.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import org.cchao.kotlintemplate.BuildConfig
import org.cchao.kotlintemplate.Constant
import org.cchao.kotlintemplate.util.DebugLog
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.AssertionError
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
internal class RetrofitUtil private constructor() {

    init {
        throw AssertionError("No instances")
    }

    companion object {

        private val RETROFIT: Retrofit

        init {

            val okHttpBuild = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                okHttpBuild.interceptors().add(Interceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    val buffer = Buffer()
                    request.body()!!.writeTo(buffer)
                    val source = response.body()!!.source()
                    source.request(java.lang.Long.MAX_VALUE)
                    DebugLog.d("Retrofit", request.method() + "-->" + request.url())
                    DebugLog.d("Retrofit-Request", buffer.readUtf8())
                    DebugLog.d("Retrofit-Response", source.buffer().clone().readUtf8())
                    response
                })
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
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

        fun <T> create(service: Class<T>): T {
            return RETROFIT.create(service)
        }
    }
}