package org.cchao.common;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class IApplication extends Application {

    private static IApplication instance;

    private boolean debug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initStetho();
    }

    private void initStetho() {
        if (!debug) {
            return;
        }
        try {
            Class cls = Class.forName("com.facebook.stetho.Stetho");
            Method method = cls.getDeclaredMethod("initializeWithDefaults", Context.class);
            method.setAccessible(true);
            method.invoke(null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IApplication getInstance() {
        return instance;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
