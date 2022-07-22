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
import com.tagnumelite.projecteintegration.api.recipe.ACustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.CustomRecipeMapper;
import com.yuo.endless.Items.ItemRegistry;
import com.yuo.endless.Recipe.*;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class EndlessFakeAddon {
    public static final String MODID = "endless";

    public static String NAME(String name) {
        return "EndlessFake" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EFExtremeCraftingMapper extends ARecipeTypeMapper<ExtremeCraftRecipe> {
        @Override
        public String getName() {
            return NAME("ExtremeCrafting");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypeRegistry.EXTREME_CRAFT_RECIPE;
        }
    }

    @CustomRecipeMapper(MODID)
    public static class EFExtremeCraftingManagerMapper extends ACustomRecipeMapper<ExtremeCraftRecipe> {
        @Override
        public String getName() {
            return NAME("ExtremeCraftingManager");
        }

        @Override
        public List<ExtremeCraftRecipe> getRecipes() {
            return ExtremeCraftingManager.getInstance().getRecipeList();
        }

        @Override
        protected List<Ingredient> getIngredients(ExtremeCraftRecipe recipe) {
            return recipe.getIngredients();
        }

        @Override
        protected ItemStack getResult(ExtremeCraftRecipe recipe) {
            return recipe.getResultItem();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EFNeutroniumCompressorMapper extends ARecipeTypeMapper<NeutroniumRecipe> {
        @Override
        public String getName() {
            return NAME("Neutronium");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypeRegistry.NEUTRONIUM_RECIPE;
        }
    }

    @CustomRecipeMapper(MODID)
    public static class EFNeutroniumCompressorManagerMapper extends ACustomRecipeMapper<NeutroniumRecipe> {
        @Override
        public String getName() {
            return NAME("NeutroniumCompressorManager");
        }

        @Override
        public List<NeutroniumRecipe> getRecipes() {
            return CompressorManager.getRecipes();
        }

        @Override
        protected List<Ingredient> getIngredients(NeutroniumRecipe recipe) {
            return recipe.getIngredients();
        }

        @Override
        protected ItemStack getResult(NeutroniumRecipe recipe) {
            return recipe.getResultItem();
        }
    }

    @ConversionProvider(MODID)
    public static class EFDataGenerator extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Endless Fake")
                    .before(ItemRegistry.neutronPile.get(), 128);
        }
    }
}
