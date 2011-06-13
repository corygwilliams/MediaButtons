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

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Allows the user to pick a new theme to use.
 */
public class ThemeConfigure extends ListActivity
implements AdapterView.OnItemClickListener {
	
    /** The preference name for the currently set theme. */
	public final static String THEME_PREF_NAME = "icon_theme";
	
	private ThemeListAdaptor mAdaptor;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAdaptor = new ThemeListAdaptor(this);
        setListAdapter(mAdaptor);
        getListView().setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // Save the theme picked.
        String sourceZip = mAdaptor.getThemeId(position);
        // Try to cache the theme so that is works even if the sdcard is gone.
        String destZip = ZipImageSource.cacheZipTheme(sourceZip);
        if (destZip == null) {
            // Failed to save it to internal storage for whatever reason.
            // Read it directly from externalStorage as a fallback.
            destZip = sourceZip;
        }
        Log.i(Widget.TAG, "Using theme in " + destZip + " original from " + sourceZip);
        SharedPreferences.Editor prefs =
            getSharedPreferences(Configure.PREFS_NAME, 0).edit();
        prefs.putString(THEME_PREF_NAME, destZip);
        prefs.commit();
        
        // Invalid the source and all widgets since the theme has changed.
        ButtonImageSource.invalidateSource();
        Widget.invalidateAllWidgets(this);

        finish();
    }
}
