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
package com.tagnumelite.projecteintegration.plugins.magic;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;

import java.util.HashSet;
import java.util.Set;

@PEIPlugin("astralsorcery")
public class PluginAstralSorcery extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new AltarMapper());
        addMapper(new GrindstoneMapper());
        addMapper(new StarlightInfusionMapper());
    }

    private static class AltarMapper extends PEIMapper {
        public AltarMapper() {
            super("Altar");
        }

        @Override
        public void setup() {
            Set<AbstractAltarRecipe> recipes = new HashSet<>();
            AltarRecipeRegistry.recipes.values().forEach(recipes::addAll);
            AltarRecipeRegistry.mtRecipes.values().forEach(recipes::addAll);

            for (AbstractAltarRecipe recipe : recipes) {
                addRecipe(recipe.getNativeRecipe());
            }
        }
    }

    private static class GrindstoneMapper extends PEIMapper {
        public GrindstoneMapper() {
            super("Grindstone", "These recipes contain chances, so it is disabled by default", true);
        }

        @Override
        public void setup() {
            Set<GrindstoneRecipe> recipes = new HashSet<>();
            recipes.addAll(GrindstoneRecipeRegistry.recipes);
            recipes.addAll(GrindstoneRecipeRegistry.mtRecipes);

            for (GrindstoneRecipe recipe : recipes) {
                addRecipe(recipe.getOutputForMatching(), recipe.getInputForRender().getApplicableItems());
            }
        }
    }

    private static class StarlightInfusionMapper extends PEIMapper {
        public StarlightInfusionMapper() {
            super("Starlight Infusion");
        }

        @Override
        public void setup() {
            Set<AbstractInfusionRecipe> recipes = new HashSet<>();
            recipes.addAll(InfusionRecipeRegistry.recipes);
            recipes.addAll(InfusionRecipeRegistry.mtRecipes);

            for (AbstractInfusionRecipe recipe : recipes) {
                addRecipe(recipe.getOutputForMatching(), recipe.getInput().getApplicableItems());
            }
        }
    }
}
