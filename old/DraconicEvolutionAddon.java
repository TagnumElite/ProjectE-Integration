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

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.init.DETags;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.RecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DraconicEvolutionAddon {
    public static final String MODID = "draconicevolution";

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class DEFusionMapper extends ARecipeTypeMapper<FusionRecipe> {
        @Override
        public String getName() {
            return "DraconicEvolutionFusionMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?>recipeType) {
            returnrecipeType == DraconicAPI.FUSION_RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(FusionRecipe recipe) {
            ArrayList<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(recipe.getCatalyst());
            ingredients.addAll(recipe.fusionIngredients().stream()
                    .filter(IFusionRecipe.IFusionIngredient::consume)
                    .map(IFusionRecipe.IFusionIngredient::get)
                    .collect(Collectors.toList()));
            return ingredients;
        }
    }

    @ObjectHolder(MODID)
    @ConversionProvider(MODID)
    public static class DEConversionProvider extends AConversionProvider {
        @ObjectHolder("dragon_heart")
        public static final Item DRAGON_HEART = null;
        @ObjectHolder("chaos_shard")
        public static final Item CHAOS_SHARD = null;

        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Draconic Evolution")
                    .before(DETags.Items.DUSTS_DRACONIUM, 2048)
                    .before(DRAGON_HEART, 262144)
                    .before(CHAOS_SHARD, 4096000);
        }
    }
}