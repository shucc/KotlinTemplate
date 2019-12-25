package org.cchao.common.utils;

import android.text.TextUtils;
import android.util.Log;

import org.cchao.common.IApplication;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class L {

    private static final String TAG = "DebugLog";

    private L() {
    }

    public static void d(String content) {
        d(TAG, content);
    }

    public static void d(String tag, String content) {
        if (!IApplication.getInstance().isDebug()) {
            return;
        }
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(content)) {
            return;
        }
        if (content.length() <= 4000) {
            Log.d(tag, content);
            return;
        }
        for (int i = 0; i < content.length(); i += 4000) {
            if (i + 4000 < content.length()) {
                Log.d(tag, content.substring(i, i + 4000));
            } else {
                Log.d(tag, content.substring(i, content.length()));
            }
        }
    }

    public static void e(String content) {
        e(TAG, content);
    }

    public static void e(String tag, String content) {
        if (!IApplication.getInstance().isDebug()) {
            return;
        }
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(content)) {
            return;
        }
        if (content.length() <= 4000) {
            Log.e(tag, content);
            return;
        }
        for (int i = 0; i < content.length(); i += 4000) {
            if (i + 4000 < content.length()) {
                Log.e(tag, content.substring(i, i + 4000));
            } else {
                Log.e(tag, content.substring(i, content.length()));
            }
        }
    }
}
