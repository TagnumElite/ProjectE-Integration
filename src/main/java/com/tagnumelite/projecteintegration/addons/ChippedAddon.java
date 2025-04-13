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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import earth.terrarium.chipped.common.recipes.ChippedRecipe;
import earth.terrarium.chipped.common.registry.ModRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class ChippedAddon {
    public static final String MODID = "chipped";

    public static String NAME(String name) {
        return "Chipped" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedWorkbenchMapper extends ARecipeTypeMapper<ChippedRecipe> {
        @Override
        public String getName() {
            return NAME("Workbench");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.WORKBENCH.get();
        }

        @Override
        public boolean convertRecipe(ChippedRecipe recipe) {
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fgm = new ArrayList<>();

            for (Ingredient ingredient : recipe.ingredients()) {
                Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
                ItemStack[] items = ingredient.getItems();
                convertIngredient(ingredient, ingMap, fgm);

                for (ItemStack item : items) {
                    mapper.addConversion(1, NSSItem.createItem(item), ingMap);
                }
            }

            return addConversionsAndReturn(fgm, true);
        }
    }
}
