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
        R.drawable.black_play,
        R.drawable.black_fastforward,
        R.drawable.black_rewind,
        R.drawable.black_next,
        R.drawable.black_previous,
        R.drawable.black_pause,
    };
    
    private static int[] sBitmapResource = new int[] {
        R.drawable.black_play_normal,
        R.drawable.black_fastforward_normal,
        R.drawable.black_rewind_normal,
        R.drawable.black_next_normal,
        R.drawable.black_previous_normal,
        R.drawable.black_pause_normal,
    };
}
