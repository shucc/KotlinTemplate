package org.cchao.kotlintemplate.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author cchen6
 * @Date on 2019/6/19
 * @Description
 */
object JsonUtil {

    private val gson = Gson()

    private val gb = GsonBuilder()

    fun toString(`object`: Any): String {
        gb.disableHtmlEscaping()
        return gb.create().toJson(`object`)
    }

    fun <T> toObject(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }

    fun <T> toList(json: String, classOfT: Class<T>): List<T> {
        return gson.fromJson(json, ListOfJson(classOfT))
    }

    private class ListOfJson<T> internal constructor(wrapper: Class<T>) : ParameterizedType {

        private val wrapped: Class<*>

        init {
            this.wrapped = wrapper
        }

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(wrapped)
        }

        override fun getRawType(): Type {
            return List::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }
}