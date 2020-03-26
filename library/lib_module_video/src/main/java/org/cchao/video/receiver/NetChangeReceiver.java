package org.cchao.video.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import org.cchao.video.BaseVideoActivity;

/**
 * @author cchen6
 * @Date on 2020/1/14
 * @Description
 */
public class NetChangeReceiver extends BroadcastReceiver {

    private BaseVideoActivity baseVideoActivity;

    public void setBaseVideoActivity(BaseVideoActivity baseVideoActivity) {
        this.baseVideoActivity = baseVideoActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            baseVideoActivity.netChanged();
        }
    }
}
