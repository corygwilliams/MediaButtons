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

/**
 * A simple struct class to hold the label and id for each theme.
 */
class ThemeId {
    /** The label is the human readable name */
    public String label;
    /** The id is the string we pass to ButtonImageSource.createSource */
    public String id;
    
    public ThemeId(String label, String id) {
        this.label = label;
        this.id = id;
    }
}
