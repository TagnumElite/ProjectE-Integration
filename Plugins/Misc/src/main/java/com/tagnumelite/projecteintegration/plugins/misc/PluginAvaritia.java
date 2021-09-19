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

@PEIPlugin("avaritia")
public class PluginAvaritia extends APEIPlugin {
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
            AvaritiaRecipeManager.EXTREME_RECIPES.values().forEach(r -> addRecipe(r.getRecipeOutput(), r.getIngredients()));
        }
    }

    private static class CompressorMapper extends PEIMapper {
        public CompressorMapper() {
            super("Compressor");
        }

        @Override
        public void setup() {
            AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().forEach(
                r -> addConversion(r.getResult(), ImmutableMap.of(PEIApi.getList(r.getIngredients()), r.getCost()))
            );
        }
    }
}
