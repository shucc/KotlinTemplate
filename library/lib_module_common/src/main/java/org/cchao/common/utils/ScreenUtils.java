package org.cchao.common.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.cchao.common.IApplication;

/**
 * @author cchen6
 * @Date on 2019/8/2
 * @Description
 */
public class ScreenUtils {

    //打开关闭弹出框
    public static final int OPEN_POP = 0;
    public static final int HIDE_POP = 1;

    private ScreenUtils() {
    }

    /**
     * 获取屏幕
     */
    private static Display getDisplay(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay();
    }

    public static int dipToPx(float dpValue) {
        final float scale = IApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 屏幕宽度
     */
    public static Data width(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(activity).getMetrics(dm);
        return new Data(dm.widthPixels, dm.density);
    }

    /**
     * 屏幕高度
     */
    public static Data height(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(activity).getMetrics(dm);
        return new Data(dm.heightPixels, dm.density);
    }

    /**
     * 设置全屏与取消全屏
     *
     * @param activity
     * @param fullscreen
     */
    public static void setFullscreen(Activity activity, boolean fullscreen) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        } else {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        }
        activity.getWindow().setAttributes(attrs);
    }

    /**
     * 设置弹出框出现与隐藏时背景透明度变化
     *
     * @param type
     */
    public static void setWindowAlpha(Activity activity, int type) {
        //设置背景颜色变暗
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (type == OPEN_POP) {
            lp.alpha = 0.6f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取顶部状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = IApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = IApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static class Data {

        public int px;

        public float dp;

        public Data(int px, float density) {
            this.px = px;
            this.dp = px / density;
        }
    }
}
