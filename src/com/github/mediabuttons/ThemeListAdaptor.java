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
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Creates the list elements for the theme list.  The list is static (but may
 * change each time we open the theme config).  All elements are enabled.
 * Each element represents one theme.
 */
public class ThemeListAdaptor implements ListAdapter {

    private Context mContext;
    private Vector<ThemeId> mThemes = new Vector<ThemeId>();
    private int mPadding;
    private int mIconSize;
    
    public ThemeListAdaptor(Context context) {
        super();
        mContext = context;
        // Ask for all themes to display.
        ButtonImageSource.appendToThemeList(mThemes);
        // Convert from dp to px.
        final float scale = mContext.getResources().getDisplayMetrics().density;
        mPadding = (int) (2 * scale + 0.5f);
        mIconSize = (int) (32 * scale + 0.5f);
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

    /**
     * Each item has all six icons in a 3x2 grid and then the text label.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        
        String text_prefix = "";
        try {
    	    ButtonImageSource source = ButtonImageSource.createSource(mThemes.get(position).id);
    	    TableLayout table = new TableLayout(mContext);
    	    TableRow row1 = new TableRow(mContext);
    	    for (int i = 0; i < Configure.NUM_ACTIONS / 2; ++i) {
    	        row1.addView(makeImageView(source.getIcon(i)));
    	    }
    	    table.addView(row1);
    	    TableRow row2 = new TableRow(mContext);
            for (int i = Configure.NUM_ACTIONS / 2; i < Configure.NUM_ACTIONS; ++i) {
                row2.addView(makeImageView(source.getIcon(i)));
            }
            table.addView(row2);
            layout.addView(table);
        } catch (InvalidTheme e) {
            text_prefix = "(Invalid Theme) ";
        }
        
        TextView text = new TextView(mContext);
    	text.setText(text_prefix + mThemes.get(position).label);
    	text.setTextSize(24);
    	text.setPadding(mPadding, mPadding, mPadding, mPadding);
    	layout.addView(text);
    	return layout;
    }
        
    /**
     * Creates an ImageView for the given bitmap.
     */
    private ImageView makeImageView(Bitmap icon) {
        ImageView image = new ImageView(mContext);
        image.setMaxWidth(mIconSize);
        image.setMaxHeight(mIconSize);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImageBitmap(icon);
        image.setPadding(mPadding, mPadding, mPadding, mPadding);
        return image;
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
