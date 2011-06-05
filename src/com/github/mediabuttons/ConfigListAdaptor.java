package com.github.mediabuttons;

import android.content.Context;
import android.database.DataSetObserver;
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

    ConfigListAdaptor(Context context, String[] labels) {
        mContext = context;
        mLabels = labels;
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
        layout.setPadding(5, 5, 5, 5);
        ImageView image = new ImageView(mContext);
        ButtonImageSource source = ButtonImageSource.getSource();
        image.setImageBitmap(source.getIcon(mContext, position, false));
        layout.addView(image);
        if (position == Configure.PLAY_PAUSE_ACTION) {
            image = new ImageView(mContext);
            image.setImageBitmap(source.getIcon(mContext, position, true));
            layout.addView(image);
        }
        TextView text = new TextView(mContext);
        text.setTextSize(24);
        text.setPadding(5, 5, 5, 5);
        text.setText(mLabels[position]);
        layout.addView(text);
        
        return layout;
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
