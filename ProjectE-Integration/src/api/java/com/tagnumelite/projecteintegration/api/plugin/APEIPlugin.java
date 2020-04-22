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

import java.util.ArrayList;
import java.util.List;

public abstract class APEIPlugin {
    public final String modid;
    public final Configuration config;
    public final String category;

    public APEIPlugin(String modid, Configuration config) {
        this.modid = modid;
        this.config = config;
        this.category = ConfigHelper.getPluginCategory(modid);
    }

    /**
     * addEMC and addMapper should be run here
     *
     * @throws Exception If the setup fails
     */
    public abstract void setup() throws Exception;

    /**
     * @return Always return a list, it may empty if the plugin has no mappers.
     * @see PEIMapper
     */
    public List<PEIMapper> getMappers() {
        return new ArrayList<>(0);
    }

    protected void addEMC(String ore, int base_emc, String extra) {
        int emc = config.getInt("emc_ore_" + ore, category, base_emc, -1, Integer.MAX_VALUE, extra);
        for (ItemStack item : OreDictionary.getOres(ore)) {
            setEMC(item, emc);
        }
    }

    protected void addEMC(String ore, int base_emc) {
        int emc = config.getInt("emc_ore_" + ore, category, base_emc, -1, Integer.MAX_VALUE, "EMC Value for all items in oredict '" + ore + '\'');
        for (ItemStack item : OreDictionary.getOres(ore)) {
            setEMC(item, emc);
        }
    }

    /**
     * @param item     {@code Item} The item to add
     * @param meta     {@code int} Metadata value of the item
     * @param base_emc {@code int} The Base EMC Value
     */
    protected void addEMC(Item item, int meta, int base_emc) {
        addEMC(new ItemStack(item, 1, meta), base_emc);
    }

    protected void addEMC(Item item, int base_emc) {
        addEMC(new ItemStack(item), base_emc);
    }

    protected void addEMC(ItemStack item, int base_emc) {
        addEMC(item, base_emc, "");
    }

    /**
     * @param item     {@code ItemStack} The item that will be given an EMC value
     * @param base_emc {@code int} The Base EMC value, will be overwritten from the
     *                 config
     * @param extra    {@code String} Extra text to go into the comment;
     */
    protected void addEMC(ItemStack item, int base_emc, String extra) {
        if (item == null)
            return;

        setEMC(item, config.getInt("emc_item_" + item.getUnlocalizedName(), category, base_emc, -1, Integer.MAX_VALUE,
            "Set the EMC for the item '" + item.getDisplayName() + "' " + extra));
    }

    protected void addEMC(String name, Object obj, int base_emc) {
        addEMC(name, obj, base_emc, "");
    }

    /**
     * @param name     {@code String} The name of the object
     * @param obj      {@code Object} The object that will have the emc attached to it.
     * @param base_emc {@code int} The Base emc of the object
     * @param extra    {@code String} Extra information to be added to the comment.
     */
    protected void addEMC(String name, Object obj, int base_emc, String extra) {
        if (obj == null)
            return;

        setEMC(obj, config.getInt("emc_" + name.toLowerCase(), category, base_emc, -1, Integer.MAX_VALUE,
            "Set the EMC value for " + name + ' ' + extra));
    }

    private void setEMC(Object obj, int emc) {
        if (emc <= 0 || obj == null)
            return;

        PEIApi.addEMCObject(obj, emc);
    }

    protected void addMapper(PEIMapper mapper) {
        if (config.getBoolean("enable_" + mapper.name.toLowerCase().replace(' ', '_') + "_mapper", category,
            !mapper.disabled_by_default, mapper.desc))
            PEIApi.addMapper(mapper);
    }
}
