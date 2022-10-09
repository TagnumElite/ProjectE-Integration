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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import earth.terrarium.chipped.recipe.ChippedRecipe;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

import static earth.terrarium.chipped.registry.ChippedRecipeTypes.*;


public class ChippedAddon {
    public static final String MODID = "chipped";

    public static String NAME(String name) {
        return "Chipped" + name + "Mapper";
    }

    public abstract static class ChippedRecipeMapper extends ARecipeTypeMapper<ChippedRecipe> {
        @Override
        public boolean convertRecipe(ChippedRecipe recipe) {
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fgm = new ArrayList<>();
            for (HolderSet<Item> tag : recipe.tags()) {
                IngredientMap<NormalizedSimpleStack> ingMap = new IngredientMap<>();
                List<Item> items = tag.stream().filter(Holder::isBound).map(Holder::value).toList();
                convertIngredient(Ingredient.of(items.stream().map(ItemStack::new)), ingMap, fgm);

                for (Item item : items) {
                    mapper.addConversion(1, NSSItem.createItem(item), ingMap.getMap());
                }
            }
            return addConversionsAndReturn(fgm, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedBotanistWorkbenchMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("BotanistWorkbench");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BOTANIST_WORKBENCH_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedGlassblowerMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("Glassblower");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == GLASSBLOWER_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedCarpentersTableMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("CarpentersTable");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == CARPENTERS_TABLE_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedLoomTableMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("LoomTable");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == LOOM_TABLE_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedMasonTableMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("MasonTable");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MASON_TABLE_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedAlchemyBenchMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("AlchemyBench");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ALCHEMY_BENCH_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ChippedMechanistWorkbenchMapper extends ChippedRecipeMapper {
        @Override
        public String getName() {
            return NAME("MechanistWorkbench");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == MECHANIST_WORKBENCH_TYPE;
        }
    }
}
