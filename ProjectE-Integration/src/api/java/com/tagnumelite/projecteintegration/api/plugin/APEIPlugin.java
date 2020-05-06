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
package com.tagnumelite.projecteintegration.api.plugin;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * This is the abstract class all {@link PEIPlugin} should extend.
 *
 * @see PEIPlugin
 * @see PEIMapper
 */
public abstract class APEIPlugin {
    public final String name;
    public final String modid;
    public final String category;
    public final Configuration config;

    /**
     * Instantiate the Plugin and fetch modid, name, category and config.
     *
     * @throws IllegalStateException    Class doesn't have a {@link PEIPlugin} annotation
     * @throws IllegalArgumentException {@link PEIPlugin#value()} is empty when it must not be
     */
    public APEIPlugin() {
        PEIPlugin plugin = getClass().getAnnotation(PEIPlugin.class);
        if (plugin == null) throw new IllegalStateException("Plugins must have a @PEIPlugin annotation");
        modid = plugin.value();
        if (modid.trim().isEmpty()) throw new IllegalArgumentException("@PEIPlugin modid may not be empty");
        name = plugin.name().isEmpty() ? modid : plugin.name();
        category = plugin.config().isEmpty() ? modid : plugin.config();
        config = PEIApi.getInstance().CONFIG;
    }

    /**
     * This is called during {@link com.tagnumelite.projecteintegration.api.internal.Phase#SETTING_UP_PLUGINS}
     * by the {@link PEIApi} and should not be called anywhere else.
     * <p>
     * {@link #addEMC} and {@link #addMapper(PEIMapper)} should be called during this setup.
     *
     * @implNote The exception is only there for logging and can be ignored.
     */
    public abstract void setup() throws Exception;

    /**
     * Calls {@link #addEMC(ItemStack, int)} on each value returned from {@link OreDictionary#getOres(String)}
     *
     * @param ore      The {@link OreDictionary} string to fetch {@link List} of {@link ItemStack} from
     * @param base_emc The default EMC value that will be assigned to the {@link OreDictionary#getOres(String)} values,
     *                 may be overwritten from the config.
     * @param extra    The comment that will be added the config option
     */
    protected void addEMC(String ore, int base_emc, String extra) {
        int emc = config.getInt("emc_ore_" + ore, category, base_emc, -1, Integer.MAX_VALUE, extra);
        for (ItemStack item : OreDictionary.getOres(ore)) {
            setEMC(item, emc);
        }
    }

    /**
     * Calls {@link #addEMC(ItemStack, int)} on each value returned from {@link OreDictionary#getOres(String)}
     *
     * @param ore      The {@link OreDictionary} string to fetch {@link List} of {@link ItemStack} from
     * @param base_emc The default EMC value that will be assigned to the {@link OreDictionary#getOres(String)} values,
     *                 may be overwritten from the config.
     */
    protected void addEMC(String ore, int base_emc) {
        int emc = config.getInt("emc_ore_" + ore, category, base_emc, -1, Integer.MAX_VALUE, "EMC Value for all items in oredict '" + ore + '\'');
        for (ItemStack item : OreDictionary.getOres(ore)) {
            setEMC(item, emc);
        }
    }

    /**
     * Gets {@link ItemStack} from the {@link Item} and metadata.
     * Calls {@link #addEMC(ItemStack, int)} using the {@link ItemStack}.
     *
     * @param item     The item to be converted to {@link ItemStack}
     * @param meta     Metadata value of the item
     * @param base_emc The Base EMC Value
     */
    protected void addEMC(Item item, int meta, int base_emc) {
        addEMC(new ItemStack(item, 1, meta), base_emc);
    }

    /**
     * Gets {@link ItemStack} from the {@link Item} and calls {@link #addEMC(ItemStack, int)} using the {@link ItemStack}
     *
     * @param item     The {@link Item} to be converted into {@link ItemStack}
     * @param base_emc The default EMC value for the {@link Item}
     */
    protected void addEMC(Item item, int base_emc) {
        addEMC(new ItemStack(item), base_emc);
    }

    /**
     * Alias for {@code #addEMC(item, base_emc, "")}
     *
     * @see #addEMC(ItemStack, int, String)
     */
    protected void addEMC(ItemStack item, int base_emc) {
        addEMC(item, base_emc, "");
    }

    /**
     * Calls {@link #setEMC(Object, int)} on the {@link ItemStack} with the EMC value from the config or default EMC.
     *
     * @param item     The {@link ItemStack} that will have EMC assigned to
     * @param base_emc The Base EMC value, will be overwritten from the config
     * @param extra    Comment string that will along side the emc config option.
     */
    protected void addEMC(ItemStack item, int base_emc, String extra) {
        if (item == null)
            return;

        setEMC(item, config.getInt("emc_item_" + item.getUnlocalizedName(), category, base_emc, -1, Integer.MAX_VALUE,
            "Set the EMC for the item '" + item.getDisplayName() + "' " + extra));
    }

    /**
     * Alias for {@link #addEMC(String, Object, int, String)}
     *
     * @see #addEMC(String, Object, int, String)
     */
    protected void addEMC(final String name, Object obj, int base_emc) {
        addEMC(name, obj, base_emc, "");
    }

    /**
     * Calls {@link #setEMC(Object, int)} on the object with the EMC acquired from the config.
     *
     * @param name     The name of the object in the config, changing the name will change the config option.
     * @param obj      The object that will have EMC applied to it.
     * @param base_emc The default EMC value that will be applied, unless overwritten by the config.
     * @param extra    Extra comment string that will along side the config option.
     */
    protected void addEMC(final String name, Object obj, int base_emc, String extra) {
        if (obj == null)
            return;

        setEMC(obj, config.getInt("emc_" + name.toLowerCase(), category, base_emc, -1, Integer.MAX_VALUE,
            "Set the EMC value for " + name + ". " + extra));
    }

    /**
     * Calls {@link PEIApi#addEMCObject(Object, int)}. Silently fails if EMC is below/equal to 0 or if obj is null.
     *
     * @param obj The object that will EMC applied to
     * @param emc The EMC to apply to the object
     */
    private void setEMC(Object obj, int emc) {
        if (emc <= 0 || obj == null)
            return;

        PEIApi.addEMCObject(obj, emc);
    }

    /**
     * Calls {@link PEIApi#addMapper(PEIMapper)} if the mapper is enabled.
     *
     * @param mapper The mapper to be added to the {@link PEIApi}
     */
    protected void addMapper(PEIMapper mapper) {
        if (mapper.name.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Mapper '%s' name may not be empty", mapper.getClass().getCanonicalName()));
        }
        if (config.getBoolean(ConfigHelper.getMapperName(mapper), category, !mapper.disabled_by_default, mapper.desc)) {
            PEIApi.addMapper(mapper);
        }
    }
}
