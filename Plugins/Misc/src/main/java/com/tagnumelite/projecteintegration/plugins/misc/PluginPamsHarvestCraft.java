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
package com.tagnumelite.projecteintegration.plugins.misc;

import com.pam.harvestcraft.item.GrinderRecipes;
import com.pam.harvestcraft.item.PresserRecipes;
import com.pam.harvestcraft.item.WaterFilterRecipes;
import com.pam.harvestcraft.tileentities.MarketData;
import com.pam.harvestcraft.tileentities.MarketItems;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.Utils;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.Arrays;
import java.util.Map;

// TODO: ...., hey Tag, who hurt you a year ago.
@PEIPlugin("harvestcraft")
public class PluginPamsHarvestCraft extends APEIPlugin {
    @Override
    public void setup() {
        addEMC("listAllfruit", 128);
        addEMC("listAllgrain", 24);
        addEMC("listAllmeatraw", 64);
        addEMC("listAllveggie", 64);
        addEMC("listAllnut", 64);
        addEMC("listAllgreenveggie", 64);
        addEMC("listAllseed", 16);
        addEMC("listAllberry", 16);

        addMapper(new MarketMapper());
        addMapper(new MachineMapper("Grinder", GrinderRecipes.class, "grindingList"));
        addMapper(new MachineMapper("Presser", PresserRecipes.class, "pressingList"));
        addMapper(new MachineMapper("Water Filter", WaterFilterRecipes.class, "waterfilterList"));
    }

    private static class MarketMapper extends PEIMapper {
        protected MarketMapper() {
            super("Market", "Enable mapper for the market?", true);
        }

        @Override
        public void setup() {
            for (int i = 0; i < MarketItems.getSize(); i++) {
                MarketData data = MarketItems.getData(i);
                ItemStack input = data.getCurrency().copy();
                input.setCount(data.getPrice());
                addRecipe(data.getItem(), input);
            }
        }
    }

    private static class MachineMapper extends PEIMapper {
        private final Class<?> machine;
        private final String field;

        public MachineMapper(String name, Class<?> machine, String field) {
            super(name, "This may not work 100% of the time because of hacky methods used to fetch the recipes!");
            this.machine = machine;
            this.field = field;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setup() {
            try {
                for (Map.Entry<ItemStack, ItemStack[]> entr : ((Map<ItemStack, ItemStack[]>) FieldUtils
                    .readStaticField(machine, field, true)).entrySet()) {
                    ItemStack[] outputs = entr.getValue();
                    ItemStack input = entr.getKey();
                    if (outputs.length == 2) {
                        if (Utils.isSameItem(outputs[0], outputs[1])) {
                            ItemStack output = outputs[0].copy();
                            output.setCount(outputs[0].getCount() + outputs[1].getCount());

                            addRecipe(output, input.copy());
                            continue;
                        }
                    }

                    addRecipe(Arrays.asList(outputs), input);
                }
            } catch (Exception e) {
                PEIApi.LOGGER.error("Failed to get HarvestCraft machine '{}' recipes: {}", name, e);
            }
        }
    }
}
