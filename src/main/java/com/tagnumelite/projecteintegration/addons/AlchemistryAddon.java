/*
 * Copyright (c) 2019-2022 TagnumElite
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

import al132.alchemistry.RecipeTypes;
import al132.alchemistry.blocks.atomizer.AtomizerRecipe;
import al132.alchemistry.blocks.combiner.CombinerRecipe;
import al132.alchemistry.blocks.evaporator.EvaporatorRecipe;
import al132.alchemistry.blocks.fission.FissionRecipe;
import al132.alchemistry.blocks.liquifier.LiquifierRecipe;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;
import java.util.stream.Collectors;

public class AlchemistryAddon {
    public static final String MODID = "alchemistry";

    public static String NAME(String name) {
        return "Alchemistry" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryAtomizerMapper extends ARecipeTypeMapper<AtomizerRecipe> {
        @Override
        public String getName() {
            return NAME("Atomizer");
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.ATOMIZER;
        }

        @Override
        public NSSInput getInput(AtomizerRecipe recipe) {
            return NSSInput.createFluid(recipe.input);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryCombinerMapper extends ARecipeTypeMapper<CombinerRecipe> {
        @Override
        public String getName() {
            return NAME("Combiner");
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.COMBINER;
        }

        @Override
        protected List<Ingredient> getIngredients(CombinerRecipe recipe) {
            return recipe.getEmptyStrippedInputs().stream().map(Ingredient::of).collect(Collectors.toList());
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
    //    public boolean canHandle(IRecipeType<?> recipeType) {
    //        return recipeType == RecipeRegistry.DISSOLVER_TYPE;
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryEvaporatorMapper extends ARecipeTypeMapper<EvaporatorRecipe> {
        @Override
        public String getName() {
            return NAME("Evaporator");
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.EVAPORATOR;
        }

        @Override
        public NSSInput getInput(EvaporatorRecipe recipe) {
            if (recipe.input == null || recipe.input.isEmpty()) return null;
            return NSSInput.createFluid(recipe.input);
        }

        @Override
        public NSSOutput getOutput(EvaporatorRecipe recipe) {
            return new NSSOutput(recipe.output);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryFusionMapper extends ARecipeTypeMapper<FissionRecipe> {
        @Override
        public String getName() {
            return NAME("Fusion");
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.FISSION;
        }

        @Override
        public NSSOutput getOutput(FissionRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryLiquifierMapper extends ARecipeTypeMapper<LiquifierRecipe> {
        @Override
        public String getName() {
            return NAME("Liquifier");
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.LIQUIFIER;
        }

        @Override
        public NSSOutput getOutput(LiquifierRecipe recipe) {
            return new NSSOutput(recipe.output);
        }
    }
}
