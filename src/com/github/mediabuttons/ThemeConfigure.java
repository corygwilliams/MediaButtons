package com.github.mediabuttons;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class ThemeConfigure extends ListActivity
implements AdapterView.OnItemClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setListAdapter(new ThemeListAdaptor(this));
        getListView().setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // Save the theme picked.
        SharedPreferences.Editor prefs =
            getSharedPreferences(Configure.PREFS_NAME, 0).edit();
        //prefs.putInt(ACTION_PREF_PREFIX + mInstanceId, position);
        prefs.commit();

        Intent resultValue = new Intent();
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
