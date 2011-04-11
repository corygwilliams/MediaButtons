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
