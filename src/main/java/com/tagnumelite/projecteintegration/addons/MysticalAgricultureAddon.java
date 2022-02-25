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

import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IReprocessorRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.RecipeTypes;
import com.blakebr0.mysticalagriculture.init.ModBlocks;
import com.blakebr0.mysticalagriculture.init.ModItems;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;

public class MysticalAgricultureAddon {
    public static final String MODID = "mysticalagriculture";

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAInfusionMapper extends ARecipeTypeMapper<IInfusionRecipe> {
        @Override
        public String getName() {
            return "MysticalAgricultureInfusionMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypes.INFUSION;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAReprocessorMapper extends ARecipeTypeMapper<IReprocessorRecipe> {
        @Override
        public String getName() {
            return "MysticalAgricultureReprocessorMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypes.REPROCESSOR;
        }
    }

    @ConversionProvider(MODID)
    public static class MysticalAgricultureConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Mystical Agriculture")
                    .before(ModItems.PROSPERITY_SHARD.get(), 128)
                    .before(ModItems.INFERIUM_ESSENCE.get(), 32)
                    .before(ModItems.SOULIUM_DUST.get(), 128)
                    .before(ModBlocks.SOULSTONE_COBBLE.get(), 16);
        }
    }
}
