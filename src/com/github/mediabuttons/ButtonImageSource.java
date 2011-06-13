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

import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * This is the base class used for retrieving widget images for a specific
 * theme.  It acts as both an interface, and as a static factory that chooses
 * the correct implementation, depending on the specific theme.
 */
public abstract class ButtonImageSource {
    /** This holds the ButtonImageSource for the current theme. */
    private static ButtonImageSource sInstance = null;
    
    /**
     * Set the image on the ImageButton for the specific action.  It may be
     * different that the Bitmap returned by getIcon, since we many want to
     * use a more complex drawable resource.
     * 
     * @param view   The RemoteViews for the widget that needs to be updated.
     * @param action_index   The action this widget represents.
     */
    abstract void setButtonIcon(RemoteViews view, int action_index);
    /**
     * Returns a bitmap the represents specific action.  It should be related
     * to the image set by setButtonIcon.
     * 
     * @param action_index   The action we want a bitmap for.
     * @return   A Bitmap representing the given action.
     */
    abstract Bitmap getIcon(int action_index);
    
    /**
     * Get the current source, as specified by the preferences.  We fall back
     * to the standard theme if the current theme if invalid.  In this case,
     * we reset the preferences to the default theme too.
     * 
     * @return   The ButtonImageSource for the current them.
     */
    public static ButtonImageSource getSource() {
        if (sInstance == null) {
            Context context = App.getContext();
        	SharedPreferences prefs =
            	context.getSharedPreferences(Configure.PREFS_NAME, 0);
            String themeId = prefs.getString(
            		ThemeConfigure.THEME_PREF_NAME, "Black");
            try {
                sInstance = ButtonImageSource.createSource(themeId);
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
    
    /**
     * Return the proper subclass for the theme identified by theme id.
     * 
     * @param themeId   The theme to get a source for.
     * @return   A subclass of buttonImageSource for the given theme id.
     * @throws InvalidTheme   If the specified theme is invalid.
     */
    public static ButtonImageSource createSource(String themeId) throws InvalidTheme {
        if (themeId.endsWith(".zip")) {
            return new ZipImageSource(themeId);
        } else {
            return new ResourceImageSource(themeId);
        }
    }
    
    /**
     * Appends all known themes to the vector.  This includes all subclasses.
     * 
     * @param themes   This vector will have all themes appended to it.  We
     *    assume it is empty to start with at this level, though subclass may
     *    not make that assumption.
     */
    public static void appendToThemeList(Vector<ThemeId> themes) {
        ResourceImageSource.appendToThemeList(themes);
        ZipImageSource.appendToThemeList(themes);
    }
    
    /** Should be called if the theme preference changes. */
    public static void invalidateSource() {
    	sInstance = null;
    }
}
