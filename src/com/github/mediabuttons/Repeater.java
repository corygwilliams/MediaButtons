package com.github.mediabuttons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Listens for a few broadcast intents that require registration and
 * forwards them to the Broadcast class.  Listens for ACTION_SCREEN_ON
 * and ACTION_HEADSET_PLUG.
 */
public class Repeater extends BroadcastReceiver {

    private static Repeater sRepeater = null;

    // Enforce singleton status with private constructor.
    private Repeater() {
        super();
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, Broadcaster.class);
        context.startService(intent);
    }

    /**
     * Created and register the repeater.  Does nothing if already started.
     * 
     * @param context   The context to register the receiver in.  Should be an
     *      application context.
     */
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

    /**
     * Stop and unregister the receiver if it is running.
     * 
     * @param context   The same context used when start() was called.
     */
    public static void stop(Context context) {
        if (sRepeater == null) {
            return;
        }
        Log.d(Widget.TAG, "Stopping repeater");
        context.unregisterReceiver(sRepeater);
        sRepeater = null;
    }
}
