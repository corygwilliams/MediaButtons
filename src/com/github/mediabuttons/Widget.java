package com.github.mediabuttons;

import com.github.mediabuttons.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
    public void onUpdate(Context context, AppWidgetManager manager,
    		int[] appWidgetIds) {
        SharedPreferences prefs =
        	context.getSharedPreferences(Configure.PREFS_NAME, 0);
        for (int id: appWidgetIds) {
        	String pref_name = Configure.ACTION_PREF_PREFIX + id;
            int action_index = prefs.getInt(pref_name, 0);
            updateWidget(context, manager, id, action_index);
        }
    }

	public static void updateWidget(Context context, AppWidgetManager manager,
			int id, int action_index) {
		
		// Create an Intent for the media button
		int keyCode = Configure.sKeyCode[action_index];
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setClass(context, Broadcaster.class);
        // The URI is unused, but serves to differentiate multiple intents so
        // that getService doesn't replace one using a different keycode.
        intent.setData(Uri.parse("http://" + keyCode));
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        PendingIntent pendingIntent = PendingIntent.getService(
        		context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(),
        		R.layout.widget);
        views.setImageViewResource(R.id.button, Configure.sImageResource[action_index]);
        views.setOnClickPendingIntent(R.id.button, pendingIntent);
        
        manager.updateAppWidget(id, views);
	}

}