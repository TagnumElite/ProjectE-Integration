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

import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.FluidExtractorRecipe;
import com.mrcrayfish.vehicle.crafting.FluidMixerRecipe;
import com.mrcrayfish.vehicle.crafting.RecipeType;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MrCrayfishVehicleModAddon {
    public static final String MODID = "vehicle"; // What a shitty mod id.

    protected static String NAME(String name) {
        return "MrCrayfishVehicleMod" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MCVMFluidExtractorMapper extends ARecipeTypeMapper<FluidExtractorRecipe> {
        @Override
        public String getName() {
            return NAME("FluidExtractor");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeType.FLUID_EXTRACTOR;
        }

        @Override
        public NSSOutput getOutput(FluidExtractorRecipe recipe) {
            return new NSSOutput(recipe.getResult().createStack());
        }

        @Override
        protected List<Ingredient> getIngredients(FluidExtractorRecipe recipe) {
            return Collections.singletonList(Ingredient.of(recipe.getIngredient()));
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MCVMFluidMixerMapper extends ARecipeTypeMapper<FluidMixerRecipe> {
        @Override
        public String getName() {
            return NAME("FluidExtractor");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeType.FLUID_MIXER;
        }

        @Override
        public NSSOutput getOutput(FluidMixerRecipe recipe) {
            return new NSSOutput(recipe.getResult().createStack());
        }

        @Override
        public NSSInput getInput(FluidMixerRecipe recipe) {
            ItemStack itemInput = recipe.getIngredient();
            FluidEntry[] fluidInputs = recipe.getInputs();

            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            ingredientMap.addIngredient(NSSItem.createItem(itemInput), itemInput.getCount());

            for (FluidEntry fluidInput : fluidInputs) {
                ingredientMap.addIngredient(NSSFluid.createFluid(fluidInput.getFluid()), fluidInput.getAmount());
            }

            return new NSSInput(ingredientMap, new ArrayList<>(), true);
        }
    }
}
