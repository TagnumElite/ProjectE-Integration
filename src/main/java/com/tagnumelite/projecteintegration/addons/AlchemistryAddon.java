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

import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlchemistryAddon {
    public static final String MODID = "alchemistry";

    @Contract(pure = true)
    public static @NotNull String NAME(String name) {
        return "Alchemistry" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryAtomizerMapper extends ARecipeTypeMapper<AtomizerRecipe> {
        @Override
        public String getName() {
            return NAME("Atomizer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.ATOMIZER_TYPE;
        }

        @Override
        public NSSInput getInput(AtomizerRecipe recipe) {
            return NSSInput.createFluid(recipe.getInput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryCombinerMapper extends ARecipeTypeMapper<CombinerRecipe> {
        @Override
        public String getName() {
            return NAME("Combiner");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.COMBINER_TYPE;
        }

        @Override
        public NSSInput getInput(CombinerRecipe recipe) {
            List<IngredientStack> ingredients = recipe.getInput();
            if (ingredients.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, ingredients);
                return null;
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (IngredientStack ingredient : ingredients) {
                if (!convertIngredient(ingredient.getCount(), ingredient.getIngredient(), ingredientMap, fakeGroupMap)) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryCompactorMapper extends ARecipeTypeMapper<CompactorRecipe> {
        @Override
        public String getName() {
            return NAME("Compactor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.COMPACTOR_TYPE;
        }

        @Override
        public NSSInput getInput(CompactorRecipe recipe) {
            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            IngredientStack ingredient = recipe.getInput();

            if (!convertIngredient(ingredient.getCount(), ingredient.getIngredient(), ingredientMap, fakeGroupMap)) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    //public static class AlchemistryDissolverMapper extends ARecipeTypeMapper<DissolverRecipe> {
    //    @Override
    //    public String getName() {
    //        return NAME("Dissolver");
    //    }
    //
    //    @Override
    //    public boolean canHandle(RecipeType<?> recipeType) {
    //        return recipeType == RecipeRegistry.DISSOLVER_TYPE;
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryFissionMapper extends ARecipeTypeMapper<FissionRecipe> {
        @Override
        public String getName() {
            return NAME("Fission");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.FISSION_TYPE;
        }

        @Override
        public NSSOutput getOutput(FissionRecipe recipe) {
            return mapOutputs(recipe.getOutput1(), recipe.getOutput2());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryFusionMapper extends ARecipeTypeMapper<FusionRecipe> {
        @Override
        public String getName() {
            return NAME("Fusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.FUSION_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryLiquifierMapper extends ARecipeTypeMapper<LiquifierRecipe> {
        @Override
        public String getName() {
            return NAME("Liquifier");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.LIQUIFIER_TYPE;
        }

        @Override
        public NSSOutput getOutput(LiquifierRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }
}
