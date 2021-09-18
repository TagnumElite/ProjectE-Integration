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

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public final class ConfigHelper {
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_PLUGINS = "plugins";

    public static String getCategory(String... txts) {
        return String.join(Configuration.CATEGORY_SPLITTER, txts);
    }

    /**
     * @param modid Modid of the mod
     * @return String CATEGORY_PLUGINS + CATEGORY_SPLITTER + key
     */
    public static String getPluginCategory(String modid) {
        return getCategory(CATEGORY_PLUGINS, modid);
    }

    /**
     *
     * @param name
     * @return
     */
    public static String getConversionName(String name) {
        return "enable_" + ConfigHelper.getConfigName(name) + "_conversions";
    }

    /**
     *
     * @param txt
     * @return
     */
    public static String getConfigName(String txt) {
        return txt.toLowerCase().replaceAll("[ \\-~`\\[{}\\]+='\";:/?.>,<!@#$%^&*()]", "_");
    }

    /**
     *
     * @param name
     * @return
     */
    public static String getMapperName(String name) {
        return "enable_" + getConfigName(name) + "_mapper";
    }

    /**
     *
     * @param mapper
     * @return
     */
    public static String getMapperName(PEIMapper mapper) {
        return getMapperName(mapper.name);
    }
}
