package org.cchao.common;

import android.app.Application;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class IApplication extends Application {

    private static IApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static IApplication getInstance() {
        return instance;
    }
}
