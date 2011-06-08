package com.github.mediabuttons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

class ResourceImageSource extends ButtonImageSource {
    @Override
    void setButtonIcon(RemoteViews view, int actionIndex, boolean isPlaying) {
        Log.i(Widget.TAG, "Setting icon");
        int resource = sImageResource[actionIndex];
        if (actionIndex == Configure.PLAY_PAUSE_ACTION && isPlaying) {
            Log.i(Widget.TAG, "Setting pause icon");
           resource = sPauseImageResource;
        }
        view.setImageViewResource(R.id.button, resource);
    }
    
    @Override
    Bitmap getIcon(Context context, int actionIndex, boolean isPlaying) {
        int resource = sBitmapResource[actionIndex];
        if (actionIndex == Configure.PLAY_PAUSE_ACTION && isPlaying) {
            resource = sPauseBitmapResource;
        }
        Drawable drawable = context.getResources().getDrawable(resource);
        return ((BitmapDrawable) drawable).getBitmap();
    }
    
    /**
     * The image resources to use for each media action.
     */
    private static int[] sImageResource = new int[] {
        R.drawable.play,  // Will be updated by handler.
        R.drawable.fastforward,
        R.drawable.rewind,
        R.drawable.next,
        R.drawable.previous,
    };
    private static int sPauseImageResource = R.drawable.pause;
    
    private static int[] sBitmapResource = new int[] {
        R.drawable.play_normal,
        R.drawable.fastforward_normal,
        R.drawable.rewind_normal,
        R.drawable.next_normal,
        R.drawable.previous_normal,
    };
    private static int sPauseBitmapResource = R.drawable.pause_normal;
}
