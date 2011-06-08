package com.github.mediabuttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public abstract class ButtonImageSource {
    private static ButtonImageSource sInstance = null;
    
    abstract void setButtonIcon(RemoteViews view, int action_index, boolean isPlaying);
    abstract Bitmap getIcon(Context context, int action_index, boolean isPlaying);
    
    public static ButtonImageSource getSource() {
        if (sInstance == null) {
            //sInstance = new ResourceImageSource();
            sInstance = new ZipImageSource("/sdcard/Android/data/com.github.mediabuttons/files/test.zip");
        }
        return sInstance;
    }
}
