package com.github.mediabuttons;

import java.util.Hashtable;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class Broadcaster extends IntentService {

    private Handler mHandler = new Handler();
    private Runnable mUpdateButton = null;
    
    public final static String BROADCAST_MEDIA_BUTTON =
        "com.github.mediabuttons.Broadcaster.BROADCAST_MEDIA_BUTTON";
    
    public Broadcaster() {
        super("Broadcaster");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        Log.i(Widget.TAG, "Got intent " + intent.getAction());
        if (intent.getAction().equals(BROADCAST_MEDIA_BUTTON)) {
            int keycode = Integer.parseInt(intent.getData().getHost());
            long upTime = SystemClock.uptimeMillis();
            long downTime = upTime - 1;
            
            Log.i(Widget.TAG, "Got keycode " + keycode);
            
            KeyEvent downKeyEvent = new KeyEvent(
                downTime, downTime, KeyEvent.ACTION_DOWN, keycode, 0);
            Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downKeyEvent);
            sendOrderedBroadcast(downIntent, null);
            
            KeyEvent upKeyEvent = new KeyEvent(
                downTime, upTime, KeyEvent.ACTION_UP, keycode, 0);
            Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upKeyEvent);
            sendOrderedBroadcast(upIntent, null);
            
            if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                startUpdater(new UpdaterRunnable(this, 5));
            }
        } else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            startUpdater(new UpdaterRunnable(this, 5));
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startUpdater(new UpdaterRunnable(this, 1));
        }
    }

    private void startUpdater(UpdaterRunnable updater) {
        if (mUpdateButton != null) {
            mHandler.removeCallbacks(mUpdateButton);
        }
        mUpdateButton = updater;
        mHandler.postDelayed(mUpdateButton, 300);
    }

    private class UpdaterRunnable implements Runnable {
        private Hashtable<Integer, RemoteViews> mViews = new Hashtable<Integer, RemoteViews>();
        private Boolean mMusicPlaying = null;
        private int mUpdateRepeat;
        private Context mContext;
        private AppWidgetManager mManager;
        private AudioManager audioManager;
        
        UpdaterRunnable(Context context, int repeat) {
            mContext = context;
            mUpdateRepeat = repeat;
            Log.i(Widget.TAG, "Creating the list of play/pause widgets.");
            mManager = AppWidgetManager.getInstance(mContext);
            ComponentName component = new ComponentName(mContext, Widget.class);
            int[] widgetIds = mManager.getAppWidgetIds(component);
            SharedPreferences prefs =
                context.getSharedPreferences(Configure.PREFS_NAME, 0);
            for (int id : widgetIds) {
                String pref_name = Configure.ACTION_PREF_PREFIX + id;
                int action_index = prefs.getInt(pref_name, -1);
                if (action_index == Configure.PLAY_PAUSE_ACTION) {
                    mViews.put(id, Widget.makeRemoteViews(mContext, id, action_index));
                }
            }
            audioManager = (AudioManager)
                    mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        
        public void run() {
            if (mContext == null) {
                Log.e(Widget.TAG, "Unable to run play/pause handler because context is null");
                return;
            }
            Log.i(Widget.TAG, "Play/pause handler called for " + mViews.size() + " widgets");
            boolean isActive = audioManager.isMusicActive();
            if (mMusicPlaying == null || mMusicPlaying != isActive) {
                mMusicPlaying = isActive;
                for (int id: mViews.keySet()) {
                    RemoteViews views = mViews.get(id);
                    Widget.setPlayPauseIcon(views, isActive);
                    mManager.updateAppWidget(id, views);
                }
            }
            if (--mUpdateRepeat > 0) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };
}
