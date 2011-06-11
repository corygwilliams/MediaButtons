package com.github.mediabuttons;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ThemeListAdaptor implements ListAdapter {

    private Context mContext;
    private Vector<ThemeId> mThemes = new Vector<ThemeId>();
    
    public ThemeListAdaptor(Context context) {
        super();
        mContext = context;
        ButtonImageSource.appendToThemeList(mThemes);
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
        return mThemes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    
    public String getThemeId(int position) {
    	return mThemes.get(position).id;
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
    	text.setText(mThemes.get(position).label);
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
