package com.github.mediabuttons;

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public abstract class ButtonImageSource {
    private static ButtonImageSource sInstance = null;
    
    abstract void setButtonIcon(RemoteViews view, int action_index);
    abstract Bitmap getIcon(Context context, int action_index);
    
    public static ButtonImageSource getSource(Context context) {
        if (sInstance == null) {
        	SharedPreferences prefs =
            	context.getSharedPreferences(Configure.PREFS_NAME, 0);
            String themeId = prefs.getString(
            		ThemeConfigure.THEME_PREF_NAME, "default");
            if (themeId.endsWith(".zip")) {
                sInstance = new ZipImageSource(themeId);
            } else {
                sInstance = new ResourceImageSource(themeId);
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
