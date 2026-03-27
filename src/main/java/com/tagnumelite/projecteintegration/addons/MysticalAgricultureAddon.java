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

import com.blakebr0.mysticalagriculture.api.crafting.IAwakeningRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IReprocessorRecipe;
import com.blakebr0.mysticalagriculture.init.ModBlocks;
import com.blakebr0.mysticalagriculture.init.ModItems;
import com.blakebr0.mysticalagriculture.init.ModRecipeTypes;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

public class MysticalAgricultureAddon {
    public static final String MODID = "mysticalagriculture";

    public static String NAME(String name) {
        return "MysticalAgriculture" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAAwakeningMapper extends ARecipeTypeMapper<IAwakeningRecipe> {
        @Override
        public String getName( ) {
            return NAME("Awakening");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.AWAKENING.get();
        }

        @Override
        public NSSInput getInput(IAwakeningRecipe recipe) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            NSSInput.Builder builder = getInputBuilder();

            builder.addIngredient(recipe.getAltarIngredient());
            recipe.getEssences().forEach(builder::addItem);

            // We want to skip the essences because that has an amount to be counted
            builder.addIngredient(ingredients.get(1)).addIngredient(ingredients.get(3))
                   .addIngredient(ingredients.get(5)).addIngredient(ingredients.get(7));

            return builder.build();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAInfusionMapper extends ARecipeTypeMapper<IInfusionRecipe> {
        @Override
        public String getName( ) {
            return NAME("Infusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.INFUSION.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAReprocessorMapper extends ARecipeTypeMapper<IReprocessorRecipe> {
        @Override
        public String getName( ) {
            return NAME("Reprocessor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.REPROCESSOR.get();
        }
    }

    @ConversionProvider(MODID)
    public static class MysticalAgricultureConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Mystical Agriculture").before(ModItems.PROSPERITY_SHARD.get(), 128)
                   .before(ModItems.INFERIUM_ESSENCE.get(), 32).before(ModItems.SOULIUM_DUST.get(), 128)
                   .before(ModItems.COGNIZANT_DUST.get(), 16384).before(ModBlocks.SOULSTONE_COBBLE.get(), 16);
        }
    }
}
