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

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.init.DETags;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class DraconicEvolutionAddon {
    public static final String MODID = "draconicevolution";

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class DEFusionMapper extends ARecipeTypeMapper<FusionRecipe> {
        @Override
        public String getName() {
            return "DraconicEvolutionFusionMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == DraconicAPI.FUSION_RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(FusionRecipe recipe) {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(recipe.getCatalyst());
            ingredients.addAll(recipe.fusionIngredients().stream()
                    .filter(IFusionRecipe.IFusionIngredient::consume)
                    .map(IFusionRecipe.IFusionIngredient::get)
                    .toList());
            return ingredients;
        }
    }

    @ConversionProvider(MODID)
    public static class DEConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Draconic Evolution")
                    .before(DETags.Items.DUSTS_DRACONIUM, 2048)
                    .before(DEContent.dragon_heart, 262144)
                    .before(DEContent.chaos_shard, 4096000);
        }
    }
}