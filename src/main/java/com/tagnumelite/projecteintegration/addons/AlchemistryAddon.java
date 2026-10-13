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

import com.smashingmods.alchemistry.common.recipe.atomizer.AtomizerRecipe;
import com.smashingmods.alchemistry.common.recipe.combiner.CombinerRecipe;
import com.smashingmods.alchemistry.common.recipe.compactor.CompactorRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.DissolverRecipe;
import com.smashingmods.alchemistry.common.recipe.dissolver.ProbabilitySet;
import com.smashingmods.alchemistry.common.recipe.fission.FissionRecipe;
import com.smashingmods.alchemistry.common.recipe.fusion.FusionRecipe;
import com.smashingmods.alchemistry.common.recipe.liquifier.LiquifierRecipe;
import com.smashingmods.alchemistry.registry.RecipeRegistry;
import com.smashingmods.alchemylib.api.item.IngredientStack;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
            return recipeType == RecipeRegistry.ATOMIZER_TYPE.get();
        }

        @Override
        public NSSInput getInput(AtomizerRecipe recipe) {
            return NSSInput.ofFluid(recipe.getInput());
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
            return recipeType == RecipeRegistry.COMBINER_TYPE.get();
        }

        @Override
        public NSSInput getInput(CombinerRecipe recipe) {
            List<IngredientStack> ingredients = recipe.getInput();
            if (ingredients.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, ingredients);
                return null;
            }

            NSSInput.Builder input = getInputBuilder();
            for (IngredientStack ingredient : ingredients) {
                input.addIngredient(ingredient.getCount(), ingredient.getIngredient());
            }
            return input.build();
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
            return recipeType == RecipeRegistry.COMPACTOR_TYPE.get();
        }

        @Override
        public NSSInput getInput(CompactorRecipe recipe) {
            IngredientStack ingredient = recipe.getInput();
            return convertSingleIngredient(ingredient.getCount(), ingredient.getIngredient());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryDissolverMapper extends ARecipeTypeMapper<DissolverRecipe> {
        @Override
        public String getName() {
            return NAME("Dissolver");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.DISSOLVER_TYPE.get();
        }

        @Override
        public NSSInput getInput(DissolverRecipe recipe) {
            return convertSingleIngredient(recipe.getInput().getCount(), recipe.getInput().getIngredient());
        }

        @Override
        public NSSOutput getOutput(DissolverRecipe recipe) {
            ProbabilitySet probabilitySet = recipe.getOutput();

            NSSOutput.Builder output = getOutputBuilder();
            probabilitySet.getProbabilityGroups().forEach(group -> group.getOutput().stream().filter(i -> !i.is(Items.AIR)).forEach(output::addItem));
            return output.build();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AlchemistryFissionMapper extends ARecipeTypeMapper<FissionRecipe> {
        @Override
        public String getName() {
            return NAME("Fission");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.FISSION_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(FissionRecipe recipe) {
            return mapOutputs(recipe.getOutput1(), recipe.getOutput2());
        }

        @Override
        protected List<Ingredient> getIngredients(FissionRecipe recipe) {
            return Collections.singletonList(Ingredient.of(recipe.getInput()));
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
            return recipeType == RecipeRegistry.FUSION_TYPE.get();
        }

        @Override
        protected List<Ingredient> getIngredients(FusionRecipe recipe) {
            ArrayList<Ingredient> ingredients = new ArrayList<>(2);
            ingredients.add(Ingredient.of(recipe.getInput1()));
            ingredients.add(Ingredient.of(recipe.getInput2()));
            return ingredients;
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
            return recipeType == RecipeRegistry.LIQUIFIER_TYPE.get();
        }

        @Override
        public NSSInput getInput(LiquifierRecipe recipe) {
            return convertSingleIngredient(recipe.getInput().getCount(), recipe.getInput().getIngredient());
        }

        @Override
        public NSSOutput getOutput(LiquifierRecipe recipe) {
           return new NSSOutput(recipe.getOutput());
        }
    }

    @ConversionProvider(MODID)
    public static class AlchemistryConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.before(ingotTag("lithium"), 312);
        }
    }
}
