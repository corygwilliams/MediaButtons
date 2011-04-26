package com.github.mediabuttons;

import java.util.ArrayList;

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

/**
 * This service receives intents from the widgets and the Repeat class and
 * broadcasts media button events as well as updating the widgets as music
 * starts or stops.
 */
public class Broadcaster extends IntentService {

    private Handler mHandler = new Handler();
    private Runnable mUpdateButton = null;

    private ArrayList<Integer> mWidgetIds = null;

    public final static String BROADCAST_MEDIA_BUTTON =
        "com.github.mediabuttons.Broadcaster.BROADCAST_MEDIA_BUTTON";
    public final static String INVALIDATE_WIDGET_LIST = 
        "com.github.mediabuttons.Broadcaster.INVALIDATE_WIDGET_LIST";

    public Broadcaster() {
        super("Broadcaster");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.d(Widget.TAG, "Got intent " + action);
        if (action.equals(BROADCAST_MEDIA_BUTTON)) {
            // Sent by the widgets.  Broadcast on their behalf.

            // We set the button up time to now and the button down time
            // to 1 millisecond in the past.  This should just look like
            // delayed routing of the intent.
            long upTime = SystemClock.uptimeMillis();
            long downTime = upTime - 1;

            int keycode = Integer.parseInt(intent.getData().getHost());
            Log.d(Widget.TAG, "Got keycode " + keycode);

            KeyEvent downKeyEvent = new KeyEvent(
                    downTime, downTime, KeyEvent.ACTION_DOWN, keycode, 0);
            Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downKeyEvent);
            // Note that sendOrderedBroadcast is needed since there is only
            // one official receiver of the media button intents at a time
            // (controlled via AudioManager) so the system needs to figure
            // out who will handle it rather than just send it to everyone.
            sendOrderedBroadcast(downIntent, null);

            KeyEvent upKeyEvent = new KeyEvent(
                    downTime, upTime, KeyEvent.ACTION_UP, keycode, 0);
            Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upKeyEvent);
            sendOrderedBroadcast(upIntent, null);

            if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                // If we are pausing or starting music, start updater to
                // check for when it actually stops or starts.
                startUpdater(5);
            }
        } else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            // When the headphones are unplugged, music normally stops.
            // Check updates for 5 seconds since we don't know when the
            // music will actually stop (or if it will).
            startUpdater(5);
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            // The screen turning on is a good time to periodically
            // double check the widgets.  We don't expect the music state to
            // actually change, so we only check once.
            startUpdater(1);
        } else if (action.equals(INVALIDATE_WIDGET_LIST)) {
            updateWidgetIds();
        }
    }

    /**
     * Send an intent (to this class) to invalidate the cached list
     * of widgets in the broadcaster.  Actually invalidation happens
     * in updateWidgetIds.
     * 
     * @param context   A context used for sending the intent.
     */
    public static void invalidateWidgetList(Context context) {
        Intent intent = new Intent(Broadcaster.INVALIDATE_WIDGET_LIST);
        intent.setClass(context, Broadcaster.class);
        context.startService(intent);
    }

    /**
     * Start the updater and cancel any previous ones.
     * 
     * @param repeat   How many times to repeat the update.
     */
    private void startUpdater(int repeat) {
        RemoteViews view = Widget.makeRemoteViews(
                this, Configure.PLAY_PAUSE_ACTION);
        if (mWidgetIds == null) {
            updateWidgetIds();
        }
        UpdaterRunnable updater = new UpdaterRunnable(
                this, view, mWidgetIds, repeat);
        if (mUpdateButton != null) {
            mHandler.removeCallbacks(mUpdateButton);
        }
        mUpdateButton = updater;
        mHandler.postDelayed(mUpdateButton, 300);
    }

    /**
     * Update the list of play/pause widgets we use for updating.
     */
    private void updateWidgetIds() {
        Log.d(Widget.TAG, "Creating the list of play/pause widgets.");
        if (mWidgetIds == null) {
            mWidgetIds = new ArrayList<Integer>();
        } else {
            mWidgetIds.clear();
        }
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName component = new ComponentName(this, Widget.class);
        int[] widgetIds = manager.getAppWidgetIds(component);
        SharedPreferences prefs =
            getSharedPreferences(Configure.PREFS_NAME, 0);
        for (int id : widgetIds) {
            String pref_name = Configure.ACTION_PREF_PREFIX + id;
            int action_index = prefs.getInt(pref_name, -1);
            if (action_index == Configure.PLAY_PAUSE_ACTION) {
                mWidgetIds.add(id);
            }
        }
    }

    /**
     * This runnable is repeatedly called for short period of time when we
     * expect the state of music playing to change.  It will update the
     * widgets as we see that state change.
     */
    private class UpdaterRunnable implements Runnable {
        private RemoteViews mView;
        private Boolean mMusicPlaying = null;
        private int mUpdateRepeat;
        private ArrayList<Integer> mWidgetIds;
        private AppWidgetManager mManager;
        private AudioManager audioManager;

        /**
         * Creates the runnable updater.  Does not schedule the first run.
         * 
         * @param context   The current context.
         * @param view   The RemoteViews to update.
         * @param widgetIds   The widgetIds to update.
         * @param repeat   The number of times to reschedule this runnable.
         */
        UpdaterRunnable(Context context, RemoteViews view,
                ArrayList<Integer> widgetIds, int repeat) {
            mView = view;
            mUpdateRepeat = repeat;
            mWidgetIds = widgetIds;
            mManager = AppWidgetManager.getInstance(context);
            audioManager = (AudioManager)
            context.getSystemService(Context.AUDIO_SERVICE);
        }

        public void run() {
            Log.d(Widget.TAG, "Play/pause handler called for " + mWidgetIds.size() + " widgets");
            boolean isActive = audioManager.isMusicActive();
            // Only bother with updating on the first pass or if the state changes.
            if (mMusicPlaying == null || mMusicPlaying != isActive) {
                mMusicPlaying = isActive;
                for (int id: mWidgetIds) {
                    Widget.setPlayPauseIcon(mView, isActive);
                    mManager.updateAppWidget(id, mView);
                }
            }
            if (--mUpdateRepeat > 0) {
                // Reschedule for 1 second later.
                mHandler.postDelayed(this, 1000);
            }
        }
    };
}
