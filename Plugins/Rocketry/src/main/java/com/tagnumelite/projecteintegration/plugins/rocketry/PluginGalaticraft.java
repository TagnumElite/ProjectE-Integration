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
package com.tagnumelite.projecteintegration.plugins.rocketry;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.GCItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

@PEIPlugin("galacticraftcore")
public class PluginGalaticraft extends APEIPlugin {
    @Override
    public void setup() {
        // Set Meteoric Iron Raw EMC to 512
        addEMC(GCItems.meteoricIronRaw, 512);

        addMapper(new CircuitFabricatorMapper());
        addMapper(new CompressorMapper());
        for (NasaWorkbenchMapper.Workbench bench : NasaWorkbenchMapper.Workbench.values()) {
            addMapper(new NasaWorkbenchMapper(bench));
        }
    }

    @SuppressWarnings("unchecked")
    private static class CircuitFabricatorMapper extends PEIMapper {
        public CircuitFabricatorMapper() {
            super("Circuit Fabricator");
        }

        @Override
        public void setup() {
            for (NonNullList<Object> inputList : CircuitFabricatorRecipes.getRecipes()) {
                List<ItemStack> inputs = new ArrayList<>(inputList.size());
                inputList.forEach(o -> {
                    if (o instanceof ItemStack)
                        inputs.add((ItemStack) o);
                    else
                        inputs.add(((List<ItemStack>) o).get(0));
                });

                //Use input array to fetch output
                ItemStack output = CircuitFabricatorRecipes.getOutputForInput(inputs);
                if (output == null || output.isEmpty()) continue;

                // We use inputList to make conversion
                addRecipe(output, inputList.toArray());
                // This is done because we need a list of ItemStack to fetch output
                // but we can use List of both ItemStack and nested list of ItemStack for recipe conversion
            }
        }
    }

    private static class CompressorMapper extends PEIMapper {
        public CompressorMapper() {
            super("Compressor");
        }

        @Override
        public void setup() {
            for (IRecipe iRecipe : CompressorRecipes.getRecipeList()) {
                if (iRecipe instanceof ShapelessOreRecipeGC) {
                    ShapelessOreRecipeGC recipe = (ShapelessOreRecipeGC) iRecipe;
                    addRecipe(recipe.getRecipeOutput(), recipe.getInput().toArray());
                } else if (iRecipe instanceof ShapedRecipesGC) {
                    ShapedRecipesGC recipe = (ShapedRecipesGC) iRecipe;
                    addRecipe(recipe.getRecipeOutput(), (Object[]) recipe.recipeItems);
                } else {
                    //This should never be called, but is there as a last case scenario
                    addRecipe(iRecipe);
                }
            }
        }
    }

    private static class NasaWorkbenchMapper extends PEIMapper {
        private final Workbench workbench;

        public NasaWorkbenchMapper(Workbench workbench) {
            super("NASA Workbench " + workbench.name());
            this.workbench = workbench;
        }

        @Override
        public void setup() {
            List<INasaWorkbenchRecipe> recipes;
            switch (workbench) {
                case ROCKET_BENCH_T1:
                    recipes = GalacticraftRegistry.getRocketT1Recipes();
                    break;
                case ROCKET_BENCH_T2:
                    recipes = GalacticraftRegistry.getRocketT2Recipes();
                    break;
                case ROCKET_BENCH_T3:
                    recipes = GalacticraftRegistry.getRocketT3Recipes();
                    break;
                case CARGO_ROCKET_BENCH:
                    recipes = GalacticraftRegistry.getCargoRocketRecipes();
                    break;
                case ASTRO_MINER_BENCH:
                    recipes = GalacticraftRegistry.getAstroMinerRecipes();
                    break;
                case BUGGY_BENCH:
                    recipes = GalacticraftRegistry.getBuggyBenchRecipes();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + workbench);
            }

            for (INasaWorkbenchRecipe recipe : recipes) {
                addRecipe(recipe.getRecipeOutput(), recipe.getRecipeInput().values().toArray());
            }
        }

        public enum Workbench {
            ROCKET_BENCH_T1,
            ROCKET_BENCH_T2,
            ROCKET_BENCH_T3,
            CARGO_ROCKET_BENCH,
            ASTRO_MINER_BENCH,
            BUGGY_BENCH,
        }
    }
}
