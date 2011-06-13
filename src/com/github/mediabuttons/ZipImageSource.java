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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * This class represents a theme that comes from a zip file.  Such
 * themes need not be registered and may change during the lifetime of the
 * widget process.  All such themes need to be found in the external
 * storage directory under "Android/data/com.github.mediabuttons/themes".
 * 
 * For more details on structure of zip themes or how to use them, see
 * doc/zip_themes.txt
 */
public class ZipImageSource extends ButtonImageSource {
    public static final String CACHED_ZIP_FILE = "cached_theme.zip";
    
    Bitmap[] mBitmaps = new Bitmap[Configure.NUM_ACTIONS];
    Bitmap mPauseBitmap;
    
    static String[] sFilenames = { "play.png", "fastforward.png",
        "rewind.png", "next.png", "previous.png", "pause.png" };
    
    /**
     * Read through the themes directory to find all *.zip files.  We do not
     * do any further validation of the theme files.
     * 
     * @param themes  The vector to append themes to.
     */
    public static void appendToThemeList(Vector<ThemeId> themes) {
        File base_dir = Environment.getExternalStorageDirectory();
        File theme_dir = new File(base_dir,
                "Android/data/com.github.mediabuttons/themes");
        String[] zip_files = theme_dir.list(new FilenameFilter() {
          public boolean accept(File dir, String filename) {
              return filename.endsWith(".zip");
          }
        });
        if (zip_files == null) {
            // The directory doesn't exist or we can't read it.
            return;
        }
        
        themes.ensureCapacity(themes.size() + zip_files.length);
        
        for (int i = 0; i < zip_files.length; ++i) {
            String name = zip_files[i];
            ThemeId theme = new ThemeId(name.substring(0, name.length() - 4),
                    theme_dir.getPath() + "/" + name);
            themes.add(theme);
        }
    }
    
    /**
     * Copy the given zip file to the internal phone storage.  Only one file
     * can be cached this way at a time.
     * 
     * @param source   The source zip file to copy to the cache.
     * @return  Returns the new theme id string to use if the caching succeeds.
     *     Returns null if it fails.
     */
    public static String cacheZipTheme(String source) {
        try {
            FileChannel in_channel = new FileInputStream(new File(source)).getChannel();
            FileChannel out_channel = App.getContext().openFileOutput(CACHED_ZIP_FILE, Context.MODE_PRIVATE).getChannel();
            out_channel.transferFrom(in_channel, 0, in_channel.size());
            out_channel.close();
            return CACHED_ZIP_FILE;
        } catch (FileNotFoundException e) {
            Log.e(Widget.TAG, "Failed to find file for caching: " + source);
        } catch (IOException e) {
            Log.e(Widget.TAG, "Error while caching " + source);
        }
        return null;
    }
    
    /**
     * Constructs a source from the given zip file theme.
     * 
     * @param themeId   Either the theme id returned by cacheZipTheme, or
     *      the path to a zip file containing a theme.
     * @throws InvalidTheme   If the zip file doesn't exist or doesn't 
     *      contain the needed data.
     */
    ZipImageSource(String themeId) throws InvalidTheme {
        try {
            FileInputStream is;
            if (themeId.equals(CACHED_ZIP_FILE)) {
                is = App.getContext().openFileInput(themeId);
            } else {
                is = new FileInputStream(new File(themeId));
            }
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String filename = ze.getName();
                Bitmap bitmap = BitmapFactory.decodeStream(zis);
                if (bitmap == null) {
                    Log.e(Widget.TAG, "Couldn't decode image in " + filename +
                            " for theme " + themeId);
                    continue;
                }
                
                boolean found = false;
                for (int i = 0; i < mBitmaps.length; ++i) {
                    if (filename.equals(sFilenames[i])) {
                        mBitmaps[i] = bitmap;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Log.e(Widget.TAG, "Didn't recognize filename " +
                            filename + " found in " + themeId);
                }
            }
            // Validate that we found all the files we need.
            boolean valid = true;
            for (int i = 0; i < mBitmaps.length; ++i) {
                if (mBitmaps[i] == null) {
                    Log.e(Widget.TAG, "Zip missing " + sFilenames[i]);
                    valid = false;
                }
            }
            if (!valid) {
                Log.e(Widget.TAG, "Error found in zip theme " + themeId);
                throw new InvalidTheme();
            }
        } catch (FileNotFoundException e) {
            Log.e(Widget.TAG, "Failed to find file " + themeId);
            throw new InvalidTheme();
        } catch (IOException e) {
            Log.e(Widget.TAG, "Error while reading " + themeId);
            throw new InvalidTheme();
        }
    }
    
    @Override
    Bitmap getIcon(int actionIndex) {
        return mBitmaps[actionIndex];
    }

    @Override
    void setButtonIcon(RemoteViews view, int actionIndex) {
        Log.i(Widget.TAG, "Setting icon (zip)");
        view.setImageViewBitmap(R.id.button, mBitmaps[actionIndex]);
    }

}
