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

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;

@PEIPlugin("avaritia")
public class PluginAvaritia extends APEIPlugin {
    private final float compressor_cost_multiplier;

    public PluginAvaritia() {
        this.compressor_cost_multiplier = config.getFloat("compressor_cost_multiplier", this.category, 1F, 0.00001F, 1F,
            "Multiplier to the EMC calculation");
    }

    @Override
    public void setup() {
        addEMC(ModItems.neutron_pile, 128);

        addMapper(new ExtremeMapper());
        addMapper(new CompressorMapper());
    }

    private static class ExtremeMapper extends PEIMapper {
        public ExtremeMapper() {
            super("Extreme Crafting Table");
        }

        @Override
        public void setup() {
            for (IExtremeRecipe recipe : AvaritiaRecipeManager.EXTREME_RECIPES.values()) {
                addRecipe(recipe.getRecipeOutput(), recipe.getIngredients().toArray());
            }
        }
    }

    private class CompressorMapper extends PEIMapper {
        public CompressorMapper() {
            super("Compressor");
        }

        @Override
        public void setup() {
            for (ICompressorRecipe recipe : AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()) {
                ItemStack output = recipe.getResult();
                if (output.isEmpty())
                    continue;

                addConversion(output, ImmutableMap.of(PEIApi.getList(recipe.getIngredients()),
                    Math.max(Math.round(recipe.getCost() * compressor_cost_multiplier), 1)));
            }
        }
    }
}
