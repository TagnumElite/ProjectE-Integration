/*
 * Copyright (c) 2019-2024 TagnumElite
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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import novamachina.exnihilosequentia.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExNihiloSequentiaAddon {
    public static final String MODID = "exnihilosequentia";

    static String NAME(String name) {
        return "ExNihiloSequentia" + name + "Mapper";
    }

    // Skipped Composting Recipes

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSCrushingMapper extends ARecipeTypeMapper<CrushingRecipe> {
        @Override
        public String getName() {
            return NAME("Crushing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.CRUSHING;
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Skips all items with change less than 100%";
        }

        @Override
        protected List<Ingredient> getIngredients(CrushingRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(CrushingRecipe recipe) {
            NSSOutput.Builder builder = NSSOutput.builder(mapper, fakeGroupManager, recipeID);
            for (ItemStackWithChance drop : recipe.getDrops()) {
                if (drop.getChance() >= 1.0f)
                    builder.addOutput(drop.getStack());
            }
            if (builder.isEmpty())
                return NSSOutput.EMPTY;
            return builder.toOutput();
        }
    }

    /// Skipped Harvesting (Crook) Recipes

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSMeltingMapper extends ARecipeTypeMapper<MeltingRecipe> {
        @Override
        public String getName() {
            return NAME("Melting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.MELTING;
        }

        @Override
        protected List<Ingredient> getIngredients(MeltingRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(MeltingRecipe recipe) {
            return new NSSOutput(recipe.getResultFluid());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSPrecipitateMapper extends ARecipeTypeMapper<PrecipitateRecipe> {
        @Override
        public String getName() {
            return NAME("Precipitate");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.PRECIPITATE;
        }

        @Override
        public NSSInput getInput(PrecipitateRecipe recipe) {
            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();

            if (!convertIngredient(recipe.getInput(), ingredientMap, fakeGroupMap))
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            if (!convertFluidIngredient(recipe.getFluid(), ingredientMap, fakeGroupMap))
                return new NSSInput(ingredientMap, fakeGroupMap, false);

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        public NSSOutput getOutput(PrecipitateRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSSiftingMapper extends ARecipeTypeMapper<SiftingRecipe> {
        @Override
        public String getName() {
            return NAME("Sifting");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Only maps guaranteed drop, ignore chanced items.";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.SIFTING;
        }

        @Override
        protected List<Ingredient> getIngredients(SiftingRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(SiftingRecipe recipe) {
            return new NSSOutput(recipe.getDrop());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSSolidifyingMapper extends ARecipeTypeMapper<SolidifyingRecipe> {
        @Override
        public String getName() {
            return NAME("Solidifying");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.SOLIDIFYING;
        }

        @Override
        public NSSOutput getOutput(SolidifyingRecipe recipe) {
            return new NSSOutput(recipe.getResult());
        }

        @Override
        public NSSInput getInput(SolidifyingRecipe recipe) {
            return NSSInput.createFluid(recipe.getFluidInTank());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSTransitionMapper extends ARecipeTypeMapper<TransitionRecipe> {
        @Override
        public String getName() {
            return NAME("Transition");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EXNRecipeTypes.TRANSITION;
        }

        @Override
        public NSSInput getInput(TransitionRecipe recipe) {
            return NSSInput.createFluid(recipe.getFluidInTank());
        }

        @Override
        public NSSOutput getOutput(TransitionRecipe recipe) {
            return new NSSOutput(recipe.getResult());
        }
    }
}
