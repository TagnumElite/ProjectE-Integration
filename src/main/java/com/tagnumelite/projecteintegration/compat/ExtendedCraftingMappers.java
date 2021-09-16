/*
 * Copyright (c) 2019-2021 TagnumElite
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

package com.tagnumelite.projecteintegration.compat;

import com.blakebr0.extendedcrafting.api.crafting.ICompressorRecipe;
import com.blakebr0.extendedcrafting.api.crafting.RecipeTypes;
import com.blakebr0.extendedcrafting.config.ModConfigs;
import com.tagnumelite.projecteintegration.api.APEIRecipeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ExtendedCraftingMappers {
    public static final String MODID = "extendedcrafting";

    @RecipeTypeMapper(requiredMods=MODID, priority = 1)
    public static class ECCompressorMapper extends APEIRecipeMapper<ICompressorRecipe> {
        @Override
        public String getName() {
            return "Compressor";
        }

        @Override
        public String getDescription() {
            return "Maps the Extended Crafting compressor recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypes.COMPRESSOR && ModConfigs.ENABLE_COMPRESSOR.get();
        }

        @Override
        protected NSSInput getInput(ICompressorRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            convertIngredient(recipe.getInputCount(), recipe.getIngredients().get(0), ingredientMap, fakeGroupMap);
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods=MODID, priority = 1)
    public static class ECEnderCrafterMapper extends BaseRecipeTypeMapper {
        @Override
        public String getName() {
            return "EnderCrafter";
        }

        @Override
        public String getDescription() {
            return "Maps extended crafting Ender Crafter recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.ENDER_CRAFTER && ModConfigs.ENABLE_ENDER_CRAFTER.get();
        }
    }

    @RecipeTypeMapper(requiredMods=MODID, priority = 1)
    public static class ECTableMapper extends BaseRecipeTypeMapper {
        @Override
        public String getName() {
            return "Table";
        }

        @Override
        public String getDescription() {
            return "Maps Extended Crafting Table recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.TABLE && ModConfigs.ENABLE_TABLES.get();
        }
    }

    @RecipeTypeMapper(requiredMods=MODID, priority = 1)
    public static class ECCombinationMapper extends BaseRecipeTypeMapper {
        @Override
        public String getName() {
            return "Combination";
        }

        @Override
        public String getDescription() {
            return "Maps Extended Crafting Combination recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == RecipeTypes.COMBINATION && ModConfigs.ENABLE_CRAFTING_CORE.get();
        }
    }
}
