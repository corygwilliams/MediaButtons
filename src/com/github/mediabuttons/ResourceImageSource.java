package com.github.mediabuttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

class ResourceImageSource extends ButtonImageSource {
    @Override
    void setButtonIcon(RemoteViews view, int actionIndex) {
        Log.i(Widget.TAG, "Setting icon");
        view.setImageViewResource(R.id.button, sImageResource[actionIndex]);
    }
    
    @Override
    Bitmap getIcon(Context context, int actionIndex) {
        int resource = sBitmapResource[actionIndex];
        Drawable drawable = context.getResources().getDrawable(resource);
        return ((BitmapDrawable) drawable).getBitmap();
    }
    
    /**
     * The image resources to use for each media action.
     */
    private static int[] sImageResource = new int[] {
        R.drawable.play,
        R.drawable.fastforward,
        R.drawable.rewind,
        R.drawable.next,
        R.drawable.previous,
        R.drawable.pause,
    };
    
    private static int[] sBitmapResource = new int[] {
        R.drawable.play_normal,
        R.drawable.fastforward_normal,
        R.drawable.rewind_normal,
        R.drawable.next_normal,
        R.drawable.previous_normal,
        R.drawable.pause_normal,
    };
}
