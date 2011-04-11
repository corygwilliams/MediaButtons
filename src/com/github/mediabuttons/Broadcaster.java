package com.github.mediabuttons;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

public class Broadcaster extends IntentService {

	public Broadcaster() {
		super("Broadcaster");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("MediaKeys", "Got intent '" + intent.getAction() + "'");
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
			int keycode = ((KeyEvent)intent.getParcelableExtra(
				Intent.EXTRA_KEY_EVENT)).getKeyCode();
			long upTime = SystemClock.uptimeMillis();
			long downTime = upTime - 1;
			
			Log.i("MediaKeys", "Got keycode " + keycode);
			
            KeyEvent downKeyEvent = new KeyEvent(
            	downTime, downTime, KeyEvent.ACTION_DOWN, keycode, 0);
			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downKeyEvent);
            sendOrderedBroadcast(downIntent, null);
            
            KeyEvent upKeyEvent = new KeyEvent(
            	downTime, upTime, KeyEvent.ACTION_UP, keycode, 0);
			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upKeyEvent);
            sendOrderedBroadcast(upIntent, null);
		}
	}

}
