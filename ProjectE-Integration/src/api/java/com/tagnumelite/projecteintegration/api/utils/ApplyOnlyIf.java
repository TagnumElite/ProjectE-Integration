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
package com.tagnumelite.projecteintegration.api.utils;

import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.StringUtils;

/**
 * The class that contains the logic for {@link OnlyIf}
 *
 * @see OnlyIf
 */
public final class ApplyOnlyIf {
    /**
     * Check if the {@link ModContainer} is to the specification of {@link OnlyIf}
     *
     * @param onlyIf       Used to compare against the mod
     * @param modContainer Mod container to compare against
     * @return A boolean that denotes whether modContainer is to specification of onlyIf
     */
    public static boolean apply(OnlyIf onlyIf, ModContainer modContainer) {
        String mod_version = modContainer.getVersion();
        if (!StringUtils.isEmpty(onlyIf.versionStartsWith().trim())) {
            if (!mod_version.startsWith(onlyIf.versionStartsWith())) {
                return false;
            }
        }
        String versionEndsWith = onlyIf.versionEndsWith().trim();
        if (!StringUtils.isEmpty(versionEndsWith)) {
            // Is the version compare string starting with a '!'. Then we assume that we must invert the comparision
            boolean invert = versionEndsWith.charAt(0) == '!';
            // Remove the '!' from string if it starts with it
            versionEndsWith = invert ? versionEndsWith.substring(1) : versionEndsWith;
            if (invert == mod_version.endsWith(versionEndsWith)) {
                return false;
            }
        }
        return true;
    }
}
