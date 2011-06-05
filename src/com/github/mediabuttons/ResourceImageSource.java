package com.github.mediabuttons;

import android.util.Log;
import android.widget.RemoteViews;

class ResourceImageSource extends ButtonImageSource {
    @Override
    void setButtonIcon(RemoteViews view, int actionIndex, boolean isPlaying) {
        int resource = sImageResource[actionIndex];
        if (actionIndex == Configure.PLAY_PAUSE_ACTION && isPlaying) {
            resource = sPauseImageResource;
        }
        view.setImageViewResource(R.id.button, resource);
    }
    
    /**
     * The image resources to use for each media action.
     */
    private static int[] sImageResource = new int[] {
        R.drawable.play,  // Will be updated by handler.
        R.drawable.fastforward,
        R.drawable.rewind,
        R.drawable.next,
        R.drawable.previous,
    };
    private static int sPauseImageResource = R.drawable.pause;
}
