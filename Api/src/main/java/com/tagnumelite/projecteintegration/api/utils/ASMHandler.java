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

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for handling {@link ASMDataTable} to fetch {@link APEIPlugin}s
 */
public final class ASMHandler {
    private final Configuration config;
    private final ASMDataTable asmDataTable;
    private final Map<String, String> failed = new HashMap<>();

    /**
     * @param config       Config to be used for enabling plugins
     * @param asmDataTable The data table to search through for plugins
     */
    public ASMHandler(Configuration config, ASMDataTable asmDataTable) {
        this.config = config;
        this.asmDataTable = asmDataTable;
    }

    /**
     * @return A list of constructed plugins
     */
    public List<APEIPlugin> getPlugins() {
        List<APEIPlugin> plugins = new ArrayList<>();
        for (ASMDataTable.ASMData asmData : asmDataTable.getAll(PEIPlugin.class.getCanonicalName())) {
            String modid = ((String) asmData.getAnnotationInfo().get("value")).toLowerCase();
            if (!Loader.isModLoaded(modid)) continue;
            try {
                Class<?> asmClass = Class.forName(asmData.getClassName());
                if (APEIPlugin.class.isAssignableFrom(asmClass)) {
                    long start = System.currentTimeMillis();
                    if (asmClass.isAnnotationPresent(OnlyIf.class)) {
                        OnlyIf onlyIf = asmClass.getAnnotation(OnlyIf.class);
                        ModContainer modcontainer = Loader.instance().getIndexedModList().get(modid);
                        if (!ApplyOnlyIf.apply(onlyIf, modcontainer)) {
                            PEIApi.LOGGER.debug("Skipping plugin '{}'", modid);
                            continue;
                        }
                    }

                    if (!config.getBoolean("enable", ConfigHelper.getPluginCategory(modid), true, "Enable the plugin"))
                        continue;

                    Class<? extends APEIPlugin> asm_instance = asmClass.asSubclass(APEIPlugin.class);
                    Constructor<? extends APEIPlugin> plugin_constructor = asm_instance.getConstructor();
                    APEIPlugin plugin = plugin_constructor.newInstance();
                    plugins.add(plugin);
                    PEIApi.LOGGER.debug("Plugin {} took {}ms to instantiate", plugin.getClass().getCanonicalName(), System.currentTimeMillis() - start);
                }
            } catch (Throwable t) {
                PEIApi.LOGGER.error("Failed to instantiate plugin {}:", asmData.getClassName(), t);
                failed.put(modid, asmData.getClassName());
            }
        }
        return plugins;
    }

    /**
     * @return A map of failed plugins, map contains 'modid, className'
     */
    public Map<String, String> getFailed() {
        return ImmutableMap.copyOf(failed);
    }
}
