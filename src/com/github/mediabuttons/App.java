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

import android.app.Application;
import android.content.Context;

/**
 * We override Application so that we can start the Repeater when the process
 * is restarted.  We can't use static initializer since we need a Context.
 */
public class App extends Application {
    private static App sInstance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Repeater.start(getApplicationContext());
    }
    
    public App() {
        sInstance = this;
    }

    public static Context getContext() {
        return sInstance;
    }

}
