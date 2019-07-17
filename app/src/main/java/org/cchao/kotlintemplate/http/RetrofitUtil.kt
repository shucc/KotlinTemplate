package org.cchao.kotlintemplate.http

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import org.cchao.kotlintemplate.BuildConfig
import org.cchao.kotlintemplate.Constant
import org.cchao.kotlintemplate.util.DebugLog
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
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

        private const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        private const val READ_TIMEOUT = "READ_TIMEOUT"
        private const val WRITE_TIMEOUT = "WRITE_TIMEOUT"

        init {

            val okHttpBuild = OkHttpClient.Builder()

            val interceptor = Interceptor { chain ->
                val request = chain.request()

                //动态修改连接超时，写入超时以及读取超时时间,在headers中设置
                var connectTimeout = chain.connectTimeoutMillis()
                var readTimeout = chain.readTimeoutMillis()
                var writeTimeout = chain.writeTimeoutMillis()
                val connectNew = request.header(CONNECT_TIMEOUT)
                val readNew = request.header(READ_TIMEOUT)
                val writeNew = request.header(WRITE_TIMEOUT)

                if (!TextUtils.isEmpty(connectNew)) {
                    connectTimeout = Integer.valueOf(connectNew!!)
                }
                if (!TextUtils.isEmpty(readNew)) {
                    readTimeout = Integer.valueOf(readNew!!)
                }
                if (!TextUtils.isEmpty(writeNew)) {
                    writeTimeout = Integer.valueOf(writeNew!!)
                }

                val response = chain
                        .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                        .proceed(request)
                if (!BuildConfig.DEBUG) {
                    response
                }
                val buffer = Buffer()
                request.body()!!.writeTo(buffer)
                val source = response.body()!!.source()
                source.request(java.lang.Long.MAX_VALUE)
                DebugLog.d("Retrofit", "============================================================================")
                DebugLog.d("Retrofit-Info", request.method() + "-->" + request.url())
                DebugLog.d("Retrofit-Request", buffer.readUtf8())
                DebugLog.d("Retrofit-Response", source.buffer().clone().readUtf8())
                DebugLog.d("Retrofit", "============================================================================")
                response
            }

            if (BuildConfig.DEBUG) {
                try {
                    val cls = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor")
                    okHttpBuild.addNetworkInterceptor(cls.newInstance() as Interceptor)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            okHttpBuild.addInterceptor(interceptor)

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