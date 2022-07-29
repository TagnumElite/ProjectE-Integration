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

import com.grimbo.chipped.recipe.ChippedRecipe;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

import static com.grimbo.chipped.recipe.ChippedSerializer.*;

public class ChippedAddon {
    // TODO: Figure out if I want to split this into separate mappers or keep it as one.
    @RecipeTypeMapper(requiredMods = "chipped", priority = 1)
    public static class CRecipeMapper extends ARecipeTypeMapper<ChippedRecipe> {
        @Override
        public String getName() {
            return "ChippedRecipeMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            return recipeType == BOTANIST_WORKBENCH_TYPE ||
                    recipeType == GLASSBLOWER_TYPE ||
                    recipeType == CARPENTERS_TABLE_TYPE ||
                    recipeType == LOOM_TABLE_TYPE ||
                    recipeType == MASON_TABLE_TYPE ||
                    recipeType == ALCHEMY_BENCH_TYPE ||
                    recipeType == MECHANIST_WORKBENCH_TYPE;
        }

        @Override
        public boolean convertRecipe(ChippedRecipe recipe) {
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fgm = new ArrayList<>();

            for (ITag<Item> tag : recipe.getTags()) {
                IngredientMap<NormalizedSimpleStack> ingMap = new IngredientMap<>();
                convertIngredient(Ingredient.of(tag), ingMap, fgm);

                for (Item item : tag.getValues()) {
                    mapper.addConversion(1, NSSItem.createItem(item), ingMap.getMap());
                }
            }

            return addConversionsAndReturn(fgm, true);
        }
    }
}
