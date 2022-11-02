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

import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import de.ellpeck.naturesaura.blocks.ModBlocks;
import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.recipes.AltarRecipe;
import de.ellpeck.naturesaura.recipes.ModRecipes;
import de.ellpeck.naturesaura.recipes.OfferingRecipe;
import de.ellpeck.naturesaura.recipes.TreeRitualRecipe;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaturesAuraAddon {
    public static final String MODID = "naturesaura";

    public static String NAME(String name) {
        return "NaturesAura" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class NAAltarMapper extends ARecipeTypeMapper<AltarRecipe> {
        @Override
        public String getName() {
            return NAME("Altar");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipes.ALTAR_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(AltarRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class NAOfferingMapper extends ARecipeTypeMapper<OfferingRecipe> {

        @Override
        public String getName() {
            return NAME("Offering");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipes.OFFERING_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(OfferingRecipe recipe) {
            return Collections.singletonList(recipe.input); // We skip the catalyst even though it does get used
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class NATreeRitualMapper extends ARecipeTypeMapper<TreeRitualRecipe> {
        @Override
        public String getName() {
            return NAME("TreeRitual");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipes.TREE_RITUAL_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(TreeRitualRecipe recipe) { // TODO: Token of ____ seems to be broken...
            // TODO: Find out why tokens are being trash, I suspect it the the aura bottles not playing nice!
            ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.ingredients.length + 17);
            ingredients.add(recipe.saplingType);
            ingredients.addAll(List.of(recipe.ingredients));
            ingredients.addAll(Collections.nCopies(16, Ingredient.of(ModBlocks.GOLD_POWDER)));
            return ingredients;
        }
    }

    @ConversionProvider(MODID)
    public static class NAConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Natures Aura")
                    .before(ModItems.GOLD_LEAF, 2)
                    .before(ModBlocks.END_FLOWER, 32)
                    .conversion(ModItems.AURA_BOTTLE).ingredient(ModItems.BOTTLE_TWO_THE_REBOTTLING).end();
        }
    }
}
