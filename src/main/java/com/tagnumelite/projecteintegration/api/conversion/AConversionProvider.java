/*
 * Copyright (c) 2019-2021 TagnumElite
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

package com.tagnumelite.projecteintegration.api.conversion;

import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.ResourceLocation;

public abstract class AConversionProvider {
    public abstract void convert(CustomConversionBuilder builder);

    protected static NormalizedSimpleStack ingotTag(String ingot) {
        return forgeTag("ingots/" + ingot);
    }

    protected static NormalizedSimpleStack gemTag(String gem) {
        return forgeTag("gems/" + gem);
    }

    protected static NormalizedSimpleStack cropsTag(String crop) {
        return forgeTag("crops/" + crop);
    }

    protected static NormalizedSimpleStack grainTag(String grain) {
        return forgeTag("grain/" + grain);
    }

    protected static NormalizedSimpleStack forgeTag(String tag) {
        return tag("forge:" + tag);
    }

    protected static NormalizedSimpleStack tag(String resourceLocation) {
        return NSSItem.createTag(new ResourceLocation(resourceLocation));
    }
}
