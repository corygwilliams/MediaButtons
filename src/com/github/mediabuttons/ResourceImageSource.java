package com.github.mediabuttons;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

class ResourceImageSource extends ButtonImageSource {
    
    /**
     * The image resources to use for each media action.
     */
    private static HashMap<String, int[]> sAllImages = new HashMap<String, int[]>();
    private static HashMap<String, int[]> sAllBitmaps = new HashMap<String, int[]>();
    
    private static int[] sBlackImageResource = new int[] {
        R.drawable.black_play,
        R.drawable.black_fastforward,
        R.drawable.black_rewind,
        R.drawable.black_next,
        R.drawable.black_previous,
        R.drawable.black_pause,
    };
    
    private static int[] sBlackBitmapResource = new int[] {
        R.drawable.black_play_normal,
        R.drawable.black_fastforward_normal,
        R.drawable.black_rewind_normal,
        R.drawable.black_next_normal,
        R.drawable.black_previous_normal,
        R.drawable.black_pause_normal,
    };
    
    private static int[] sSilverImageResource = new int[] {
        R.drawable.silver_play,
        R.drawable.silver_fastforward,
        R.drawable.silver_rewind,
        R.drawable.silver_next,
        R.drawable.silver_previous,
        R.drawable.silver_pause,
    };
    
    private static int[] sSilverBitmapResource = new int[] {
        R.drawable.silver_play_normal,
        R.drawable.silver_fastforward_normal,
        R.drawable.silver_rewind_normal,
        R.drawable.silver_next_normal,
        R.drawable.silver_previous_normal,
        R.drawable.silver_pause_normal,
    };
    
    private int[] mImages;
    private int[] mBitmaps;
    
    ResourceImageSource(String themeId) {
        mImages = sAllImages.get(themeId);
        mBitmaps = sAllBitmaps.get(themeId);
    }
    
    @Override
    void setButtonIcon(RemoteViews view, int actionIndex) {
        Log.i(Widget.TAG, "Setting icon");
        view.setImageViewResource(R.id.button, mImages[actionIndex]);
    }
    
    @Override
    Bitmap getIcon(Context context, int actionIndex) {
        int resource = mBitmaps[actionIndex];
        Drawable drawable = context.getResources().getDrawable(resource);
        return ((BitmapDrawable) drawable).getBitmap();
    }
    
    static {
        sAllImages.put("Black", sBlackImageResource);
        sAllBitmaps.put("Black", sBlackBitmapResource);
        sAllImages.put("Silver", sSilverImageResource);
        sAllBitmaps.put("Silver", sSilverBitmapResource);
    }
 
}
