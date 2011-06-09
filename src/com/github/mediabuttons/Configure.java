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

import android.app.Activity;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Configure activity for creation of a new widget.  Lets the user choose
 * which media action (play/pause, next, rewind, etc.) the widget will perform.
 */
public class Configure extends Activity 
implements AdapterView.OnItemClickListener {

    private int mInstanceId;

    public static String PREFS_NAME = "com.github.mediabuttons.prefs";
    public static String ACTION_PREF_PREFIX = "widget_action";

    /**
     * The keycodes to use for each media action.
     */
    public static int[] sKeyCode = new int[] {
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
        KeyEvent.KEYCODE_MEDIA_REWIND,
        KeyEvent.KEYCODE_MEDIA_NEXT,
        KeyEvent.KEYCODE_MEDIA_PREVIOUS,
        // This last event is for the pause version of the play/pause button.
        // It does not show up in the config list.
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
    };

    public static final int PLAY_PAUSE_ACTION = 0;
    public static final int NUM_ACTIONS = sKeyCode.length;
    // This action is the version of the play/pause action with a pause icon.
    public static final int PAUSE_PLAY_ACTION = NUM_ACTIONS - 1;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure we default to canceled in case user hits back.
        setResult(RESULT_CANCELED);

        mInstanceId = getIntent().getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (mInstanceId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setContentView(R.layout.configure);
        // Hook the ListView up to the item text.  Note that button labels is
        // only 6 long, while sKeyCode is 6 long because both versions of the
        // play/pause action are represented as a single selection in the
        // configuration list.
        ListView view = 
            ((ListView) findViewById(R.id.action_list));
        String[] button_labels = getResources().getStringArray(
                R.array.button_labels);
        view.setAdapter(new ConfigListAdaptor(this, button_labels));
        view.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // Save the action picked.
        SharedPreferences.Editor prefs =
            getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(ACTION_PREF_PREFIX + mInstanceId, position);
        prefs.commit();

        // Force update since we don't get a onUpdated in this case.
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        Widget.updateWidget(this, manager, mInstanceId, position);
        Broadcaster.invalidateWidgetList(this);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mInstanceId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
