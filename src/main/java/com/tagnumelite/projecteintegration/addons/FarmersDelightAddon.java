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
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.tag.ForgeTags;

public class FarmersDelightAddon {
    public static final String MODID = "farmersdelight";

    static String NAME(String name) {
        return "FarmersDelight" + name + "Mapper";
    }

    // TODO: Cooking pot seems to implement special logic, figure out what this means
    // Never played with this mod before.
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FDCookingPotMapper extends ARecipeTypeMapper<CookingPotRecipe> {
        @Override
        public String getName() {
            return NAME("CookingPot");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.COOKING.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FDCuttingBoardMapper extends ARecipeTypeMapper<CuttingBoardRecipe> {

        @Override
        public String getName() {
            return NAME("CuttingBoard");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.CUTTING.get();
        }

        @Override
        public NSSOutput getOutput(CuttingBoardRecipe recipe) {
            return mapOutputs(recipe.getResults().toArray());
        }
    }

    @ConversionProvider(MODID)
    public static class FDConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Set defaults conversions for Farmer's Delight")
                    .before(ForgeTags.CROPS_RICE, 1)
                    .before(ModItems.RICE_PANICLE.get(), 1)
                    .before(ModItems.STRAW.get(), 1)
                    .before(cropsTag("tomato"), 32)
                    .before(cropsTag("onion"), 32)
                    .before(cropsTag("cabbage"), 32)
                    .before(ModItems.HAM.get(), 64)
                    .before(ModBlocks.BROWN_MUSHROOM_COLONY.get(), 32)
                    .before(ModBlocks.RED_MUSHROOM_COLONY.get(), 32);
        }
    }
}
