/*
 * Copyright (c) 2019-2023 TagnumElite
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

package com.tagnumelite.projecteintegration.addons;

import cofh.lib.fluid.FluidIngredient;
import cofh.thermal.core.init.TCoreRecipeTypes;
import cofh.thermal.core.util.recipes.machine.*;
import cofh.thermal.lib.util.recipes.ThermalRecipe;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class ThermalAddon {
    public static final String MODID = "thermal";

    protected static String NAME(String name) {
        return "SomtingThermal" + name + "Mapper";
    }

    public static abstract class ThermalRecipeMapper<R extends ThermalRecipe> extends ARecipeTypeMapper<R> {
        @Override
        public NSSOutput getOutput(R recipe) {
            List<ItemStack> outputItems = recipe.getOutputItems();
            List<Float> outputItemChances = recipe.getOutputItemChances();

            NSSOutput.Builder builder = getOutputBuilder();
            builder.addOutputs(recipe.getOutputFluids());

            if (outputItems != null) {
                if (outputItemChances == null) {
                    builder.addOutputs(outputItems);
                } else {
                    for (int i = 0; i < outputItemChances.size(); i++) {
                        Float itemChance = outputItemChances.get(i);
                        if (itemChance >= 1.0F) { // TODO: Floor and multiply by chance
                            builder.addOutput(outputItems.get(i));
                        }
                    }
                }
            }

            return builder.toOutput();
        }

        @Override
        public NSSInput getInput(R recipe) {
            List<Ingredient> inputItems = recipe.getInputItems();
            List<FluidIngredient> inputFluids = recipe.getInputFluids();

            if ((inputItems == null || inputItems.isEmpty()) && (inputFluids == null || inputFluids.isEmpty())) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: {} - {}", recipeID, inputItems, inputFluids);
                return null;
            }

            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            if (inputItems != null) {
                for (Ingredient ingredient : inputItems) {
                    if (!convertIngredient(ingredient, ingredientMap, fakeGroupMap)) {
                        return new NSSInput(ingredientMap, fakeGroupMap, false);
                    }
                }
            }

            if (inputFluids != null) {
                for (FluidIngredient ingredient : inputFluids) {
                    if (!convertFluidIngredient(List.of(ingredient.getFluids()), ingredientMap, fakeGroupMap)) {
                        return new NSSInput(ingredientMap, fakeGroupMap, false);
                    }
                }
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalBottlerMapper extends ThermalRecipeMapper<BottlerRecipe> {
        @Override
        public String getName() {
            return NAME("Bottler");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_BOTTLER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalBrewerMapper extends ThermalRecipeMapper<BrewerRecipe> {
        @Override
        public String getName() {
            return NAME("Brewer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_BREWER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalCentrifugeMapper extends ThermalRecipeMapper<BrewerRecipe> {
        @Override
        public String getName() {
            return NAME("Centrifuge");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_CENTRIFUGE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalChillerMapper extends ThermalRecipeMapper<ChillerRecipe> {
        @Override
        public String getName() {
            return NAME("Chiller");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_CHILLER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalCrucibleMapper extends ThermalRecipeMapper<CrucibleRecipe> {
        @Override
        public String getName() {
            return NAME("Crucible");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_CRUCIBLE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalFurnaceMapper extends ThermalRecipeMapper<FurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("Furnace");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_FURNACE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalInsolatorMapper extends ThermalRecipeMapper<InsolatorRecipe> {
        @Override
        public String getName() {
            return NAME("Insolator");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_INSOLATOR;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalPressMapper extends ThermalRecipeMapper<PressRecipe> {
        @Override
        public String getName() {
            return NAME("Press");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_PRESS;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalPulverizerMapper extends ThermalRecipeMapper<PulverizerRecipe> {
        @Override
        public String getName() {
            return NAME("Pulverizer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_PULVERIZER;
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalPulverizerRecycleMapper extends ThermalRecipeMapper<PulverizerRecycleRecipe> {
        @Override
        public String getName() {
            return NAME("PulverizerRecycle");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_PULVERIZER_RECYCLE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalPyrolyzerMapper extends ThermalRecipeMapper<PyrolyzerRecipe> {
        @Override
        public String getName() {
            return NAME("Pyrolyzer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_PYROLYZER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalRefineryMapper extends ThermalRecipeMapper<RefineryRecipe> {
        @Override
        public String getName() {
            return NAME("Refinery");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_REFINERY;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalSawmillMapper extends ThermalRecipeMapper<SawmillRecipe> {
        @Override
        public String getName() {
            return NAME("Sawmill");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_SAWMILL;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalSmelterMapper extends ThermalRecipeMapper<SmelterRecipe> {
        @Override
        public String getName() {
            return NAME("Smelter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_SMELTER;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ThermalSmelterRecycleMapper extends ThermalRecipeMapper<SmelterRecycleRecipe> {
        @Override
        public String getName() {
            return NAME("SmelterRecycle");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == TCoreRecipeTypes.RECIPE_SMELTER_RECYCLE;
        }
    }
}
