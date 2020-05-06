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
import com.google.common.collect.ImmutableSet;
import com.tagnumelite.projecteintegration.api.internal.Phase;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ASMHandler;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * ProjectE Integeration API
 *
 * @author TagnumElite (Tagan Hoyle https://tagnumelite.com)
 */
public class PEIApi {
    public static final String MODID = "projecteintegration";
    public static final String APIID = MODID + "api";
    public static final String NAME = "ProjectE Integration";
    public static final String VERSION = "@VERSION@";
    public static final String UPDATE_JSON = "https://raw.githubusercontent.com/TagnumElite/ProjectE-Integration/1.12.x/update.json";
    public static final Logger LOG = LogManager.getLogger(APIID);
    public static final IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
    private static final Map<Ingredient, Object> INGREDIENT_CACHE = new HashMap<>();
    private static final Map<List<?>, Object> LIST_CACHE = new HashMap<>();
    private static final IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
    private static final Set<PEIMapper> MAPPERS = new HashSet<>();
    private static final Map<Object, Integer> EMC_MAPPERS = new HashMap<>();
    private static final Map<ResourceLocation, Object> RESOURCE_MAP = new HashMap<>();
    public static int mapped_conversions = 0;
    private static boolean LOCK_EMC_MAPPER = false;
    private static Phase PHASE = Phase.NULL;
    private static PEIApi INSTANCE;
    public final Configuration CONFIG;
    private final Map<String, String> FAILED_PLUGINS = new HashMap<>();
    private final Map<String, String> FAILED_MAPPERS = new HashMap<>();
    private final List<APEIPlugin> PLUGINS = new ArrayList<>();
    private boolean LOADED = false;

    /**
     * If you need the instance, use {@link #getInstance()}.
     *
     * @param config  The {@link Configuration} to use during initialization
     * @param asmData The {@link ASMDataTable} to be used for fetching plugins
     * @throws IllegalStateException Tried to create an Instance of the API when one existed already
     */
    public PEIApi(Configuration config, ASMDataTable asmData) {
        if (INSTANCE != null)
            throw new IllegalStateException("Tried to create an instance of the API when one existed already!");

        PHASE = Phase.INITIALIZING;
        CONFIG = config;
        LOG.info("Starting Phase: Initialization");
        final long startTime = System.currentTimeMillis();
        ASMHandler handler = new ASMHandler(config, asmData);
        PLUGINS.addAll(handler.getPlugins());
        FAILED_PLUGINS.putAll(handler.getFailed());
        final long endTime = System.currentTimeMillis();
        LOG.info("Finished Phase: Initialization. Took {}ms", (endTime - startTime));
        PHASE = Phase.WAITING;
        INSTANCE = this;
    }

    /**
     * @return The current phase of the api
     */
    public static Phase getPhase() {
        return PHASE;
    }

    /**
     * @return Returns the current {@link PEIApi} Instance
     */
    public static PEIApi getInstance() {
        return INSTANCE;
    }

    /**
     * @param mapper {@code PEIMapper} The mapper to add
     */
    public static void addMapper(PEIMapper mapper) {
        MAPPERS.add(mapper);
    }

    public static Set<PEIMapper> getMappers() {
        return ImmutableSet.copyOf(MAPPERS);
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

        if (LIST_CACHE.containsKey(list))
            return LIST_CACHE.get(list);

        Object obj = new Object();

        for (Object object : list) {
            if (object == null)
                continue;

            if (object instanceof Ingredient)
                if (object == Ingredient.EMPTY)
                    continue;
                else
                    object = getIngredient((Ingredient) object);
            else if (object instanceof ItemStack && ((ItemStack) object).isEmpty())
                continue;
            else if (object instanceof FluidStack && ((FluidStack) object).amount == 0)
                continue;

            conversion_proxy.addConversion(1, obj, ImmutableMap.of(object, 1));
        }

        LIST_CACHE.put(list, obj);
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

    public static void clearCache() {
        RESOURCE_MAP.clear();
        INGREDIENT_CACHE.clear();
        LIST_CACHE.clear();
        MAPPERS.clear();
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
        PHASE = Phase.SETTING_UP_PLUGINS;
        LOG.info("Starting Phase: Setting up plugins");
        final long startTime = System.currentTimeMillis();
        for (APEIPlugin plugin : PLUGINS) {
            PEIApi.LOG.debug("Running Plugin for Mod: {}", plugin.modid);
            try {
                plugin.setup();
            } catch (Throwable t) {
                LOG.error("Failed to run Plugin for '{}': {}", plugin.modid, t);
                FAILED_PLUGINS.put(plugin.modid, plugin.getClass().getCanonicalName());
                t.printStackTrace();
            }
        }

        registerEMCObjects();
        LOG.info("Added {} Mappers", getMappers().size());
        PHASE = Phase.WAITING;
        final long endTime = System.currentTimeMillis();
        LOG.info("Finished Phase: Setting up plugins. Took {}ms", (endTime - startTime));
    }

    /**
     *
     */
    public void setupMappers() {
        if (LOADED) return;
        PHASE = Phase.SETTING_UP_MAPPERS;
        LOG.info("Starting Phase: Setting Up Mappers");
        final long startTime = System.currentTimeMillis();
        for (PEIMapper mapper : getMappers()) {
            PEIApi.LOG.debug("Running Mapper: {} ({})", mapper.name, mapper);
            try {
                mapper.setup();
            } catch (Throwable t) {
                LOG.error("Mapper '{}' ({}) Failed to run: {}", mapper.name, mapper, t);
                FAILED_MAPPERS.put(mapper.name, mapper.getClass().getCanonicalName());
                t.printStackTrace();
            }
        }

        LOG.info("Added {} Conversions", mapped_conversions);

        clearCache();
        PLUGINS.clear();
        LOADED = true;

        final long endTime = System.currentTimeMillis();
        LOG.info("Finished Phase: Setting Up Mappers. Took {}ms", (endTime - startTime));
        PHASE = Phase.FINISHED;
    }
}
