package com.github.mediabuttons;

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
            		ThemeConfigure.THEME_PREF_NAME, "");
            if (themeId == "default") {
            	sInstance = new ResourceImageSource();
            } else {
            	sInstance = new ZipImageSource(themeId);
            }
        }
        return sInstance;
    }
    
    public static void invalidateSource() {
    	sInstance = null;
    }
}
