package org.cchao.common.utils;

import android.content.res.Configuration;

import org.cchao.common.IApplication;

import java.util.Locale;

public class LanguageUtils {

    private LanguageUtils() {
    }

    public static String getStringByLocal(int resId, String localStr) {
        return getStringByLocal(resId, new Locale(localStr));
    }

    public static String getStringByLocal(int resId, Locale locale) {
        Configuration configuration = new Configuration(IApplication.getInstance().getResources().getConfiguration());
        configuration.setLocale(locale);
        return IApplication.getInstance().createConfigurationContext(configuration).getResources().getString(resId);
    }
}
