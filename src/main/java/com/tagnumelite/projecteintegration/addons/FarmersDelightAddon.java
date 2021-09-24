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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.AConversionProvider;
import com.tagnumelite.projecteintegration.api.APEIRecipeMapper;
import com.tagnumelite.projecteintegration.api.ConversionProvider;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;
import vectorwing.farmersdelight.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.registry.ModItems;
import vectorwing.farmersdelight.utils.tags.ForgeTags;

public class FarmersDelightAddon {
    public static final String MODID = "farmersdelight";

    static String NAME(String name) {
        return "FarmersDelight"+name+"Mapper";
    }

    // TODO: Cooking pot seems to implement special logic, figure out what this means
    // Never played with this mod before.
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FDCookingPotMapper extends APEIRecipeMapper<CookingPotRecipe> {
        @Override
        public String getName() {
            return NAME("CookingPot");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == CookingPotRecipe.TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FDCuttingBoardMapper extends APEIRecipeMapper<CuttingBoardRecipe> {

        @Override
        public String getName() {
            return NAME("CuttingBoard");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == CuttingBoardRecipe.TYPE;
        }

        @Override
        protected NSSOutput getOutput(CuttingBoardRecipe recipe) {
            return mapOutputs(recipe.getResults().toArray());
        }
    }

    @ConversionProvider(MODID)
    public static class FDConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Set defaults conversions for Farmer's Delight")
                    .before(ForgeTags.CROPS_RICE, 1)
                    .before(ModItems.STRAW.get(), 1);
        }
    }
}
