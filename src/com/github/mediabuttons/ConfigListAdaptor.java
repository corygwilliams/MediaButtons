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

public class ConfigListAdaptor implements ListAdapter {

    private String[] mLabels;
    private Context mContext;
    private int mPadding;
    private int mIconSize;

    ConfigListAdaptor(Context context, String[] labels) {
        mContext = context;
        mLabels = labels;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        ButtonImageSource source = ButtonImageSource.getSource(mContext);
        Bitmap bitmap = source.getIcon(mContext, position);
        layout.addView(makeImageView(bitmap));
        
        if (position == Configure.PLAY_PAUSE_ACTION) {
            bitmap = source.getIcon(mContext, Configure.PAUSE_PLAY_ACTION);
            layout.addView(makeImageView(bitmap));
        }
        
        TextView text = new TextView(mContext);
        text.setTextSize(24);
        text.setPadding(mPadding, mPadding, mPadding, mPadding);
        text.setText(mLabels[position]);
        layout.addView(text);
        
        return layout;
    }
    
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
