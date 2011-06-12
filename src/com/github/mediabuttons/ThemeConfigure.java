package com.github.mediabuttons;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class ThemeConfigure extends ListActivity
implements AdapterView.OnItemClickListener {
	
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
        String destZip = ZipImageSource.cacheZipTheme(sourceZip);
        if (destZip == null) {
            // failed to save it to internal storage for whatever reason.
            // Read it directly from externalStorage as a fallback.
            destZip = sourceZip;
        }
        Log.i(Widget.TAG, "Using theme in " + destZip + " original from " + sourceZip);
        Log.i(Widget.TAG, "Look in " + App.getContext().getFilesDir());
        SharedPreferences.Editor prefs =
            getSharedPreferences(Configure.PREFS_NAME, 0).edit();
        prefs.putString(THEME_PREF_NAME, destZip);
        prefs.commit();
        ButtonImageSource.invalidateSource();
        Widget.invalidateAllWidgets(this);

        finish();
    }
}
