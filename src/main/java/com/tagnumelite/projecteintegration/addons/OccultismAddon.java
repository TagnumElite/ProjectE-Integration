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

import com.klikli_dev.occultism.crafting.recipe.CrushingRecipe;
import com.klikli_dev.occultism.crafting.recipe.RitualRecipe;
import com.klikli_dev.occultism.crafting.recipe.SpiritFireRecipe;
import com.klikli_dev.occultism.crafting.recipe.SpiritTradeRecipe;
import com.klikli_dev.occultism.registry.OccultismRecipes;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;

public class OccultismAddon {
    public static final String MODID = "occultism";

    public static String NAME(String name) {
        return "Occultism" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class OccultismCrushingMapper extends ARecipeTypeMapper<CrushingRecipe> {
        @Override
        public String getName() {
            return NAME("Crushing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == OccultismRecipes.CRUSHING_TYPE.get();
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    //public static class OccultismMinerMapper extends ARecipeTypeMapper<MinerRecipe> {
    //    @Override
    //    public String getName() {
    //        return NAME("Miner");
    //    }

    //    @Override
    //    public boolean canHandle(RecipeType<?> recipeType) {
    //        return recipeType == OccultismRecipes.CRUSHING_TYPE.get();
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class OccultismRitualMapper extends ARecipeTypeMapper<RitualRecipe> {
        @Override
        public String getName() {
            return NAME("Ritual");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == OccultismRecipes.RITUAL_TYPE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class OccultismSpiritFireMapper extends ARecipeTypeMapper<SpiritFireRecipe> {
        @Override
        public String getName() {
            return NAME("SpiritFire");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == OccultismRecipes.SPIRIT_FIRE_TYPE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class OccultismSpiritTradeMapper extends ARecipeTypeMapper<SpiritTradeRecipe> {
        @Override
        public String getName() {
            return NAME("SpiritTrade");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == OccultismRecipes.SPIRIT_TRADE_TYPE.get();
        }
    }

    //@ConversionProvider(MODID)
    //public static class OccultismConversionProvider extends AConversionProvider {
    //    @Override
    //    public void convert(CustomConversionBuilder builder) {
    //        builder.comment("default conversions for occultism")
    //                .before(OccultismItems.TALLOW.get(), 16)
    //                .before(OccultismItems.AFRIT_ESSENCE.get(), 64)
    //                .before(#forge:ingots/iesnium, 512);
    //    }
    //}
}
