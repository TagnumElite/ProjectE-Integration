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

import com.blakebr0.extendedcrafting.api.crafting.ICompressorRecipe;
import com.blakebr0.extendedcrafting.api.crafting.IEnderCrafterRecipe;
import com.blakebr0.extendedcrafting.api.crafting.IFluxCrafterRecipe;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.config.ModConfigs;
import com.blakebr0.extendedcrafting.crafting.recipe.CombinationRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;

public class ExtendedCraftingAddon {
    public static final String MODID = "extendedcrafting";

    public static String NAME(String name) {
        return "ExtendedCrafting" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECCompressorMapper extends ARecipeTypeMapper<ICompressorRecipe> {
        @Override
        public String getName() {
            return NAME("Compressor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.COMPRESSOR.get() && ModConfigs.ENABLE_COMPRESSOR.get();
        }

        @Override
        public NSSInput getInput(ICompressorRecipe recipe) {
            return convertSingleIngredient(recipe.getCount(0), recipe.getIngredients().getFirst());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECEnderCrafterMapper extends ARecipeTypeMapper<IEnderCrafterRecipe> {
        @Override
        public String getName() {
            return NAME("EnderCrafter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.ENDER_CRAFTER.get() && ModConfigs.ENABLE_ENDER_CRAFTER.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECFluxCrafterMapper extends ARecipeTypeMapper<IFluxCrafterRecipe> {
        @Override
        public String getName() {
            return NAME("FluxCrafter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.FLUX_CRAFTER.get() && ModConfigs.ENABLE_FLUX_CRAFTER.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECTableMapper extends ARecipeTypeMapper<ITableRecipe> {
        @Override
        public String getName() {
            return NAME("Table");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.TABLE.get() && ModConfigs.ENABLE_TABLES.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECCombinationMapper extends ARecipeTypeMapper<CombinationRecipe> {
        @Override
        public String getName() {
            return NAME("Combination");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.COMBINATION.get() && ModConfigs.ENABLE_CRAFTING_CORE.get();
        }
    }
}
