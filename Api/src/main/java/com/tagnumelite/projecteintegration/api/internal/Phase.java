/*
 * Copyright (c) 2019-2020 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tagnumelite.projecteintegration.api.internal;

import com.tagnumelite.projecteintegration.api.PEIApi;

/**
 * Used to determine the current phase of the
 * {@link PEIApi}
 */
public enum Phase {
    /**
     * The API hasn't started yet, you shouldn't do anything during this phase.
     */
    NULL,
    /**
     * The API is currently not doing anything and waiting for other things to happen.
     */
    WAITING,
    /**
     * The API is currently being initialized, it will be fetching plugins during this stage.
     */
    INITIALIZING,
    /**
     * This API is currently setting up plugins
     */
    SETTING_UP_PLUGINS,
    /**
     * The API is currently setting up mappers
     */
    SETTING_UP_MAPPERS,
    /**
     * The API is finished setting up everything and adding all EMC and recipes.
     */
    FINISHED
}
