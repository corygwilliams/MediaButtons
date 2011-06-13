/*
 * Copyright (C) 2011 Cory Williams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mediabuttons;

import java.util.HashMap;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * This class represents a theme that is built into the res section of the
 * app.  Each such theme needs to be registered in this class.
 */
class ResourceImageSource extends ButtonImageSource {
    
    /**
     * The image resources to use for each media action for each theme.
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
    
    // Register the resource for each theme.
    static {
        sAllImages.put("Black", sBlackImageResource);
        sAllBitmaps.put("Black", sBlackBitmapResource);
        sAllImages.put("Silver", sSilverImageResource);
        sAllBitmaps.put("Silver", sSilverBitmapResource);
    }
    
    /** These are the resources to use for the theme of the object. */
    private int[] mImages;
    private int[] mBitmaps;
    
    /**
     * Create a source for the given named theme.
     * 
     * @param themeId   The theme name.
     * @throws InvalidTheme   Thrown if there is no theme of the given name.
     */
    ResourceImageSource(String themeId) throws InvalidTheme {
        if (!sAllImages.containsKey(themeId)) {
            Log.e(Widget.TAG, "Invalid resource theme id: " + themeId);
            throw new InvalidTheme();
        }
        mImages = sAllImages.get(themeId);
        mBitmaps = sAllBitmaps.get(themeId);
    }
    
    @Override
    void setButtonIcon(RemoteViews view, int actionIndex) {
        Log.i(Widget.TAG, "Setting icon");
        view.setImageViewResource(R.id.button, mImages[actionIndex]);
    }
    
    /**
     * For the resource based themes, the icon is different from the
     * drawable we set on the widget.  We use the normal state of each
     * icon as the static version here.
     */
    @Override
    Bitmap getIcon(int actionIndex) {
        int resource = mBitmaps[actionIndex];
        return BitmapFactory.decodeResource(
                App.getContext().getResources(), resource);
    }

    /**
     * We manually add each theme we know about.
     */
    public static void appendToThemeList(Vector<ThemeId> themes) {
        themes.add(new ThemeId("Black", "Black"));
        themes.add(new ThemeId("Silver", "Silver"));
    }
}
