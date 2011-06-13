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

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Creates the list elements for the main config activity for widget creation.
 * The list consists of one entry for each action.  It is static and all
 * elements are always enabled.
 */
public class ConfigListAdaptor implements ListAdapter {

    private String[] mLabels;
    private Context mContext;
    private int mPadding;
    private int mIconSize;

    ConfigListAdaptor(Context context, String[] labels) {
        mContext = context;
        mLabels = labels;
        // Convert from dp to px.
        final float scale = mContext.getResources().getDisplayMetrics().density;
        mPadding = (int) (4 * scale + 0.5f);
        mIconSize = (int) (64 * scale + 0.5f);
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
        // No data of note to return.
        return null;
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
     * The View we create for each element consists of one (or two for
     * pause/play) images that represent the current theme for each action.
     * We also put in a text label for each action.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        ButtonImageSource source = ButtonImageSource.getSource();
        Bitmap bitmap = source.getIcon(position);
        layout.addView(makeImageView(bitmap));
        
        if (position == Configure.PLAY_PAUSE_ACTION) {
            // Display both states of the widget.
            bitmap = source.getIcon(Configure.PAUSE_PLAY_ACTION);
            layout.addView(makeImageView(bitmap));
        }
        
        TextView text = new TextView(mContext);
        text.setTextSize(24);
        text.setPadding(mPadding, mPadding, mPadding, mPadding);
        text.setText(mLabels[position]);
        layout.addView(text);
        
        return layout;
    }
    
    /**
     * Creates the ImageView icon for the given bitmap.
     * @param icon   The Bitmap to display on the ImageView
     * @return   An ImageView all set to go.
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
        // We have a static dataset, so ignore.
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // We have a static dataset, so ignore.        
    }

}
