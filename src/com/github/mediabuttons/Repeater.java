package com.github.mediabuttons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class Repeater extends BroadcastReceiver {

    private static Repeater sRepeater = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, Broadcaster.class);
        context.startService(intent);
    }

    public static void start(Context context) {
        if (sRepeater != null) {
            return;
        }
        Log.d(Widget.TAG, "Starting repeater");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        sRepeater = new Repeater();
        context.registerReceiver(sRepeater, filter);
    }
    
    public static void stop(Context context) {
        if (sRepeater == null) {
            return;
        }
        Log.d(Widget.TAG, "Stopping repeater");
        context.unregisterReceiver(sRepeater);
        sRepeater = null;
    }
}
