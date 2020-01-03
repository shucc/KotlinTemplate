package org.cchao.common.utils

import android.content.res.Configuration

import org.cchao.common.IApplication

import java.util.Locale

object LanguageUtils {

    fun getStringByLocal(resId: Int, localStr: String): String {
        return getStringByLocal(resId, Locale(localStr))
    }

    fun getStringByLocal(resId: Int, locale: Locale): String {
        val configuration = Configuration(IApplication.instance.resources.configuration)
        configuration.setLocale(locale)
        return IApplication.instance.createConfigurationContext(configuration).resources.getString(resId)
    }
}
