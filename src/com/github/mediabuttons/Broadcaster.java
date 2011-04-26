package com.github.mediabuttons;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
        Log.d(Widget.TAG, "Got intent " + intent.getAction());
        if (intent.getAction().equals(BROADCAST_MEDIA_BUTTON)) {
            int keycode = Integer.parseInt(intent.getData().getHost());
            long upTime = SystemClock.uptimeMillis();
            long downTime = upTime - 1;
            
            Log.d(Widget.TAG, "Got keycode " + keycode);
            
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
                startUpdater(5);
            }
        } else if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            startUpdater(5);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startUpdater(1);
        }
    }

    private void startUpdater(int repeat) {
        RemoteViews view = Widget.makeRemoteViews(this, Configure.PLAY_PAUSE_ACTION);
        UpdaterRunnable updater = new UpdaterRunnable(this, view, repeat);
        if (mUpdateButton != null) {
            mHandler.removeCallbacks(mUpdateButton);
        }
        mUpdateButton = updater;
        mHandler.postDelayed(mUpdateButton, 300);
    }

    private class UpdaterRunnable implements Runnable {
        private RemoteViews mView;
        private ArrayList<Integer> mWidgetIds = new ArrayList<Integer>();
        private Boolean mMusicPlaying = null;
        private int mUpdateRepeat;
        private AppWidgetManager mManager;
        private AudioManager audioManager;
        
        UpdaterRunnable(Context context, RemoteViews view, int repeat) {
            mView = view;
            mUpdateRepeat = repeat;
            Log.d(Widget.TAG, "Creating the list of play/pause widgets.");
            mManager = AppWidgetManager.getInstance(context);
            ComponentName component = new ComponentName(context, Widget.class);
            int[] widgetIds = mManager.getAppWidgetIds(component);
            SharedPreferences prefs =
                context.getSharedPreferences(Configure.PREFS_NAME, 0);
            for (int id : widgetIds) {
                String pref_name = Configure.ACTION_PREF_PREFIX + id;
                int action_index = prefs.getInt(pref_name, -1);
                if (action_index == Configure.PLAY_PAUSE_ACTION) {
                    mWidgetIds.add(id);
                }
            }
            audioManager = (AudioManager)
                    context.getSystemService(Context.AUDIO_SERVICE);
        }
        
        public void run() {
            Log.d(Widget.TAG, "Play/pause handler called for " + mWidgetIds.size() + " widgets");
            boolean isActive = audioManager.isMusicActive();
            if (mMusicPlaying == null || mMusicPlaying != isActive) {
                mMusicPlaying = isActive;
                for (int id: mWidgetIds) {
                    Widget.setPlayPauseIcon(mView, isActive);
                    mManager.updateAppWidget(id, mView);
                }
            }
            if (--mUpdateRepeat > 0) {
                mHandler.postDelayed(this, 1000);
            }
        }
    };
}
