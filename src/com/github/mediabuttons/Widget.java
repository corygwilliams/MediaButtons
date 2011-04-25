/*
 * Copyright (C) 2011 Cory Williams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mediabuttons;

import com.github.mediabuttons.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

    public final static String TAG = "MediaButtons";
    
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "onDisable called");
        Repeater.stop(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled called");
        Repeater.start(context);
    }

	@Override
    public void onUpdate(Context context, AppWidgetManager manager,
    		int[] appWidgetIds) {
        super.onUpdate(context, manager, appWidgetIds);
        Log.i(TAG, "Updating for " + appWidgetIds.length + " widgets");
        Repeater.start(context);
        if (appWidgetIds.length == 0) {
            Log.w(TAG, "No widgets to update?");
            return;
        }
        SharedPreferences prefs =
        	context.getSharedPreferences(Configure.PREFS_NAME, 0);
        for (int id: appWidgetIds) {
        	String pref_name = Configure.ACTION_PREF_PREFIX + id;
            int action_index = prefs.getInt(pref_name, -1);
            if (action_index != -1) {
                updateWidget(context, manager, id, action_index);
            } else {
                Log.w(TAG, "Invalid id with no pref " + id);
            }
        }
        // TODO send invalidate
    }
	
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i(TAG, "Deleting " + appWidgetIds.length + " widgets");
        SharedPreferences.Editor prefs =
            context.getSharedPreferences(Configure.PREFS_NAME, 0).edit();
        for (int id: appWidgetIds) {
            String pref_name = Configure.ACTION_PREF_PREFIX + id;
            prefs.remove(pref_name);
            // TODO send invalidate
        }
    }

    public static RemoteViews makeRemoteViews(Context context, int id, int action_index) {
        Log.d(TAG, "Constructing widget " + id + " with action " + action_index);
        
        int keyCode = Configure.sKeyCode[action_index];
        Intent intent = new Intent(Broadcaster.BROADCAST_MEDIA_BUTTON);
        intent.setClass(context, Broadcaster.class);
        // The URI is not the right fit for the keycode data, but the URI
        // has to be unique so that PendingIntent doesn't override the intents
        // we create for each keycode.
        intent.setData(Uri.parse("http://" + keyCode));
        PendingIntent pendingIntent = PendingIntent.getService(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        if (action_index == Configure.PLAY_PAUSE_ACTION) {
            AudioManager audioManager = (AudioManager)
                    context.getSystemService(Context.AUDIO_SERVICE);
            setPlayPauseIcon(views, audioManager.isMusicActive());
        } else {
            views.setImageViewResource(R.id.button, Configure.sImageResource[action_index]);
        }
        views.setOnClickPendingIntent(R.id.button, pendingIntent);     
        
        return views;
    }
    
	public static void updateWidget(Context context, AppWidgetManager manager,
			int id, int action_index) {
	    Log.d(TAG, "Updating widget " + id);
        manager.updateAppWidget(id, makeRemoteViews(context, id, action_index));
	}
	
	public static void setPlayPauseIcon(RemoteViews views, boolean isPlaying) {
		if (isPlaying) {
			views.setImageViewResource(R.id.button, R.drawable.pause);
		} else {
			views.setImageViewResource(R.id.button, R.drawable.play);
		}
	}
}