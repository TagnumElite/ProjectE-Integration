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
package com.tagnumelite.projecteintegration.api;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ASMHandler;
import com.tagnumelite.projecteintegration.api.utils.IngredientHandler;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * ProjectE Integration API
 *
 * @author <a href="https://tagnumelite.com">TagnumElite (Tagan Hoyle)</a>
 */
public class PEIApi {
    public static final String MODID = "projecteintegration";
    public static final String APIID = MODID + "api";
    public static final String NAME = "ProjectE Integration";
    public static final String VERSION = "3.0.1";
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/TagnumElite/ProjectE-Integration/1.12.x/update.json";
    public static final Logger LOGGER = LogManager.getLogger(APIID);

    public static final IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
    private static final Map<Ingredient, Object> INGREDIENT_CACHE = new HashMap<>();
    //private static final Map<List<?>, Object> LIST_CACHE = new HashMap<>();
    private static final IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
    private static final Map<Object, Integer> EMC_MAPPERS = new HashMap<>();
    private static final Map<ResourceLocation, Object> RESOURCE_MAP = new HashMap<>();
    public static int mapped_conversions = 0;
    private static boolean LOCK_EMC_MAPPER = false;
    private static PEIApi INSTANCE;
    public final Configuration CONFIG;
    private final Map<String, String> FAILED_PLUGINS = new HashMap<>();
    private final Map<String, String> FAILED_MAPPERS = new HashMap<>();
    private final List<APEIPlugin> PLUGINS = new ArrayList<>();

    private boolean LOADED = false;

    /** Is Debugging mode activated. */
    public static boolean DEBUG = false;

    /**
     * If you need the instance, use {@link #getInstance()}.
     *
     * @param config  The {@link Configuration} to use during initialization
     * @param asmData The {@link ASMDataTable} to be used for fetching plugins
     * @throws IllegalStateException Tried to create an Instance of the API when one existed already
     */
    public PEIApi(Configuration config, ASMDataTable asmData) {
        if (INSTANCE != null) throw new IllegalStateException("Tried to create an instance of the API when one existed already!");
        INSTANCE = this;

        CONFIG = config;
        DEBUG = config.getBoolean("debug", "general", false, "Enable debugging mode");
        LOGGER.info("Starting Phase: Initialization");
        final long startTime = System.currentTimeMillis();
        ASMHandler handler = new ASMHandler(config, asmData);
        PLUGINS.addAll(handler.getPlugins());
        FAILED_PLUGINS.putAll(handler.getFailed());
        final long endTime = System.currentTimeMillis();
        LOGGER.info("Finished Phase: Initialization. Took {}ms", (endTime - startTime));
    }

    /**
     * @return Returns the current {@link PEIApi} Instance
     * @throws IllegalStateException The api hasn't been instantiated yet
     */
    public static PEIApi getInstance() {
        if (INSTANCE == null) throw new IllegalStateException("PEIApi hasn't been instantiated yet");
        return INSTANCE;
    }

    public static void addEMCObject(Object object, int emc) {
        if (LOCK_EMC_MAPPER)
            return;

        EMC_MAPPERS.put(object, emc);
    }

    public static Map<Object, Integer> getEMCObjects() {
        return ImmutableMap.copyOf(EMC_MAPPERS);
    }

    public static void registerEMCObjects() {
        for (Entry<Object, Integer> entry : EMC_MAPPERS.entrySet()) {
            Object obj = entry.getKey();
            if (obj instanceof ItemStack)
                emc_proxy.registerCustomEMC((ItemStack) obj, (long) entry.getValue());
            else
                emc_proxy.registerCustomEMC(obj, (long) entry.getValue());
        }

        EMC_MAPPERS.clear();
        LOCK_EMC_MAPPER = true;
    }

    /**
     * @param ingredient {@code Ingredient} The ingredient to Object from
     * @return Returns {@code null} if the Ingredient is empty or null
     */
    public static Object getIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient == Ingredient.EMPTY)
            return null;

        if (INGREDIENT_CACHE.containsKey(ingredient))
            return INGREDIENT_CACHE.get(ingredient);

        Object obj = new Object();

        for (ItemStack stack : ingredient.getMatchingStacks()) {
            if (stack == null || stack.isEmpty())
                continue;

            conversion_proxy.addConversion(1, obj, ImmutableMap.of(stack, 1));
        }

        INGREDIENT_CACHE.put(ingredient, obj);
        return obj;
    }

    public static Object getList(List<?> list) {
        if (list == null || list.isEmpty())
            return null;

        // I don't think this is needed.
        //if (LIST_CACHE.containsKey(list))
        //    return LIST_CACHE.get(list);

        Object obj = new Object();

        for (Object object : list) {
            if (object == null) continue;

            SizedObject<Object> input = IngredientHandler.convert(object);
            if (input.object == null || input.amount == 0) continue;

            conversion_proxy.addConversion(1, obj, ImmutableMap.of(input.object, input.amount));
        }

        //LIST_CACHE.put(list, obj);
        return obj;
    }

    /**
     * @param resource The Resource to add EMC for
     * @param emc      EMC
     * @return An object to be used in EMC calculation
     */
    public static Object addResource(ResourceLocation resource, long emc) {
        if (RESOURCE_MAP.containsKey(resource))
            return RESOURCE_MAP.get(resource);

        if (emc <= 0)
            return null;

        Object obj = new Object();
        emc_proxy.registerCustomEMC(obj, emc);
        RESOURCE_MAP.put(resource, obj);

        return obj;
    }

    /**
     * Gets the resource location from the map, returns null if doesn't exist You
     * need to {@link #addResource(ResourceLocation, long)} to add the resource to the map
     *
     * @param resource {@code ResourceLocation} The resource location of the entity
     * @return The object linked to the resource
     */
    public static Object getResource(ResourceLocation resource) {
        return RESOURCE_MAP.get(resource);
    }

    private static void clearCache() {
        RESOURCE_MAP.clear();
        INGREDIENT_CACHE.clear();
        //LIST_CACHE.clear();
        IngredientHandler.clearHandlers();
        for (Map<?, ?> cachedMap : CACHED_MAPS) {
            try {
                cachedMap.clear();
            } catch (UnsupportedOperationException e) {
                LOGGER.warn("Failed to clear map: {}", cachedMap, e);
            }
        }
        CACHED_MAPS.clear();
        for (Collection<?> cachedCollection : CACHED_COLLECTIONS) {
            try {
                cachedCollection.clear();
            } catch (UnsupportedOperationException e) {
                LOGGER.warn("Failed to clear collection: {}", cachedCollection, e);
            }
        }
        CACHED_COLLECTIONS.clear();
    }

    private static final List<Map<?, ?>> CACHED_MAPS = new ArrayList<>();
    private static final List<Collection<?>> CACHED_COLLECTIONS = new ArrayList<>();

    /**
     * When the time comes to clear all caches, all cached maps will have {@link Map#clear()} called
     * @param map The mapped to be cleared later
     */
    public static void addCachedMap(Map<?, ?> map) {
        CACHED_MAPS.add(map);
    }

    /**
     * Adds the collection to a list of collections that will be {@link Collection#clear()} later on down the line.
     * @param collection The collection to be cleared later
     */
    public static void addCachedCollection(Collection<?> collection) {
        CACHED_COLLECTIONS.add(collection);
    }

    public Map<String, String> getFailedMappers() {
        return ImmutableMap.copyOf(FAILED_MAPPERS);
    }

    public Map<String, String> getFailedPlugins() {
        return ImmutableMap.copyOf(FAILED_PLUGINS);
    }

    /**
     *
     */
    public void setupPlugins() {
        LOGGER.info("Starting Phase: Setting up plugins");
        final long startTime = System.currentTimeMillis();

        for (APEIPlugin plugin : PLUGINS) {
            debugLog("Running Plugin for Mod: {}", plugin.modid);
            try {
                long start = System.currentTimeMillis();
                plugin.setup();
                LOGGER.debug("Plugin {} took {}ms to setup", plugin.getClass().getCanonicalName(), System.currentTimeMillis() - start);
            } catch (Throwable t) {
                LOGGER.error("Failed to run Plugin for '{}': {}", plugin.modid, t);
                FAILED_PLUGINS.put(plugin.modid, plugin.getClass().getCanonicalName());
                t.printStackTrace();
            }
        }

        registerEMCObjects();
        final long endTime = System.currentTimeMillis();
        LOGGER.info("Finished Phase: Setting up plugins. Took {}ms", (endTime - startTime));
    }

    /**
     *
     */
    public void setupMappers() {
        if (LOADED) return;
        LOGGER.info("Starting Phase: Setting Up Mappers for {} plugins", PLUGINS.size());
        final long startTime = System.currentTimeMillis();

        List<PEIMapper> mappers = PLUGINS.stream().map(APEIPlugin::call).flatMap(List::stream).collect(Collectors.toList());
        for (PEIMapper mapper : mappers) {
            debugLog("Running Mapper: {} ({})", mapper.name, mapper);
            try {
                mapper.setup();
            } catch (Throwable t) {
                LOGGER.error("Mapper '{}' ({}) Failed to run: {}", mapper.name, mapper, t);
                FAILED_MAPPERS.put(mapper.name, mapper.getClass().getCanonicalName());
                t.printStackTrace();
            }
        }

        LOGGER.info("Added {} Conversions", mapped_conversions);

        clearCache();
        PLUGINS.clear();
        LOADED = true;

        final long endTime = System.currentTimeMillis();
        LOGGER.info("Finished Phase: Setting Up Mappers. Took {}ms", (endTime - startTime));
    }

    public static void debugLog(String message, Object... args){
        if (DEBUG) {
            LOGGER.info(message, args);
        } else {
            LOGGER.debug(message, args);
        }
    }
}
