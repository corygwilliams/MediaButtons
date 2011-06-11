package com.github.mediabuttons;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public abstract class ButtonImageSource {
    private static ButtonImageSource sInstance = null;
    
    abstract void setButtonIcon(RemoteViews view, int action_index);
    abstract Bitmap getIcon(Context context, int action_index);
    
    public static ButtonImageSource getSource(Context context) {
        if (sInstance == null) {
        	SharedPreferences prefs =
            	context.getSharedPreferences(Configure.PREFS_NAME, 0);
            String themeId = prefs.getString(
            		ThemeConfigure.THEME_PREF_NAME, "Black");
            try {
                if (themeId.endsWith(".zip")) {
                    sInstance = new ZipImageSource(themeId);
                } else {
                    sInstance = new ResourceImageSource(themeId);
                }
            } catch (InvalidTheme e) { 
                Log.e(Widget.TAG, "Reverting to default theme");
                // Update Prefs.
                SharedPreferences.Editor writeable_prefs = prefs.edit();
                writeable_prefs.putString(ThemeConfigure.THEME_PREF_NAME,
                        "Black");
                writeable_prefs.commit();
                // Toast notification to let the user know what happened.
                Toast.makeText(context, R.string.theme_error, Toast.LENGTH_SHORT).show();
                try {
                    sInstance = new ResourceImageSource("Black");
                } catch (InvalidTheme e1) {
                    Log.e(Widget.TAG, "NO BLACK THEME!  Time to crash.");
                }
            }
        }
        return sInstance;
    }
    
    static void appendToThemeList(Vector<ThemeId> themes) {
        ResourceImageSource.appendToThemeList(themes);
        ZipImageSource.appendToThemeList(themes);
    }
    
    public static void invalidateSource() {
    	sInstance = null;
    }
}
