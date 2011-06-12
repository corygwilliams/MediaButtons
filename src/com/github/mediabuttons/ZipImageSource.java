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

public class ZipImageSource extends ButtonImageSource {
    public static final String CACHED_ZIP_FILE = "cached_theme.zip";
    
    Bitmap[] mBitmaps = new Bitmap[Configure.NUM_ACTIONS];
    Bitmap mPauseBitmap;
    
    static String[] sFilenames = { "play.png", "fastforward.png",
        "rewind.png", "next.png", "previous.png", "pause.png" };
    
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
    
    ZipImageSource(String source) throws InvalidTheme {
        try {
            FileInputStream is;
            if (source.equals(CACHED_ZIP_FILE)) {
                is = App.getContext().openFileInput(source);
            } else {
                is = new FileInputStream(new File(source));
            }
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String filename = ze.getName();
                Bitmap bitmap = BitmapFactory.decodeStream(zis);
                
                boolean found = false;
                for (int i = 0; i < mBitmaps.length; ++i) {
                    if (filename.equals(sFilenames[i])) {
                        mBitmaps[i] = bitmap;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Log.e(Widget.TAG, "Didn't recognize filename " + filename);
                }
            }
            boolean valid = true;
            for (int i = 0; i < mBitmaps.length; ++i) {
                if (mBitmaps[i] == null) {
                    Log.e(Widget.TAG, "Zip missing " + sFilenames[i]);
                    valid = false;
                }
            }
            if (!valid) {
                Log.e(Widget.TAG, "Error found in zip theme " + source);
                throw new InvalidTheme();
            }
        } catch (FileNotFoundException e) {
            Log.e(Widget.TAG, "Failed to find file " + source);
            throw new InvalidTheme();
        } catch (IOException e) {
            Log.e(Widget.TAG, "Error while reading " + source);
            throw new InvalidTheme();
        }
    }
    
    @Override
    Bitmap getIcon(Context context, int actionIndex) {
        return mBitmaps[actionIndex];
    }

    @Override
    void setButtonIcon(RemoteViews view, int actionIndex) {
        Log.i(Widget.TAG, "Setting icon (zip)");
        view.setImageViewBitmap(R.id.button, mBitmaps[actionIndex]);
    }

}
