package com.github.mediabuttons;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        SharedPreferences.Editor prefs =
            getSharedPreferences(Configure.PREFS_NAME, 0).edit();
        prefs.putString(THEME_PREF_NAME, mAdaptor.getThemeId(position));
        prefs.commit();
        ButtonImageSource.invalidateSource();
        // TODO redraw all widgets.

        //Intent resultValue = new Intent();
        //setResult(RESULT_OK, resultValue);
        finish();
    }
}
