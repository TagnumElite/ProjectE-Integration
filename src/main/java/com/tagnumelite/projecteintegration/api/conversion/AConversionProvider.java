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

import com.tagnumelite.projecteintegration.datagen.PEICustomConversionProvider;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.ResourceLocation;

/**
 * An abstract class for generating custom conversions for ProjectE. Use {@link ConversionProvider} to denote your class
 * as a conversion provider. There are a few utility functions for getting {@link NormalizedSimpleStack} from tags.
 */
public abstract class AConversionProvider {
    /**
     * @param ingot
     * @return
     */
    protected static NormalizedSimpleStack dustTag(String dust) {
        return forgeTag("dusts/" + dust);
    }

    /**
     * @param ingot
     * @return
     */
    protected static NormalizedSimpleStack ingotTag(String ingot) {
        return forgeTag("ingots/" + ingot);
    }

    /**
     * @param gem
     * @return
     */
    protected static NormalizedSimpleStack gemTag(String gem) {
        return forgeTag("gems/" + gem);
    }

    /**
     * @param crop
     * @return
     */
    protected static NormalizedSimpleStack cropsTag(String crop) {
        return forgeTag("crops/" + crop);
    }

    /**
     * @param grain
     * @return
     */
    protected static NormalizedSimpleStack grainTag(String grain) {
        return forgeTag("grain/" + grain);
    }

    /**
     * @param tag
     * @return
     */
    protected static NormalizedSimpleStack forgeTag(String tag) {
        return tag("forge:" + tag);
    }

    /**
     * @param resourceLocation
     * @return
     */
    protected static NormalizedSimpleStack tag(String resourceLocation) {
        return NSSItem.createTag(new ResourceLocation(resourceLocation));
    }

    /**
     * Used by {@link PEICustomConversionProvider} add custom conversions
     *
     * @param builder The builder used for this specific mod.
     */
    public abstract void convert(CustomConversionBuilder builder);
}
