package com.github.mediabuttons;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ThemeListAdaptor implements ListAdapter {

    private Context mContext;
    private String[] mLabels;
    private String[] mThemeIds;
    
    public ThemeListAdaptor(Context context) {
        mContext = context;
        File base_dir = Environment.getExternalStorageDirectory();
        File theme_dir = new File(base_dir, 
        		"Android/data/com.github.mediabuttons/themes");
        theme_dir.mkdirs();
        String[] zip_files = theme_dir.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".zip");
			}
        });
        
        mLabels = new String[zip_files.length + 1];
        mThemeIds = new String[zip_files.length + 1];
        
        mLabels[0] = "Default";
        mThemeIds[0] = "default";
        for (int i = 0; i < zip_files.length; ++i) {
        	String name = zip_files[i];
        	mLabels[i + 1] = name.substring(0, name.length() - 4);
        	mThemeIds[i + 1] = theme_dir.getPath() + "/" + name;
        }
    }
    
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        return mLabels.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    
    public String getThemeId(int position) {
    	return mThemeIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView text = (TextView) convertView;
    	if (text == null) {
    		text = new TextView(mContext);
    	}
    	text.setText(mLabels[position]);
    	text.setTextSize(24);
    	return text;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    	// Data is static, so ignore.
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // Data is static, so ignore.
    }

}
