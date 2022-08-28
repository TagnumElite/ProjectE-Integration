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

import com.legacy.blue_skies.data.BlueSkiesData;
import com.legacy.blue_skies.data.objects.alchemy.AlchemyRecipe;
import com.legacy.blue_skies.data.objects.alchemy.CatylistRecipe;
import com.legacy.blue_skies.data.objects.alchemy.TransmuteRecipe;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ACustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.CustomRecipeMapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlueSkiesAddon {
    public static final String MODID = "blue_skies";

    public static String NAME(String name) {
        return "BlueSkies" + name + "Mapper";
    }

    @CustomRecipeMapper(MODID)
    public static class BSAlchemyTableMapper extends ACustomRecipeMapper<AlchemyRecipe> {
        @Override
        public String getName() {
            return NAME("AlchemyTable");
        }

        @Override
        public List<AlchemyRecipe> getRecipes() {
            return new ArrayList<>(BlueSkiesData.ALCHEMY_RECIPES.getData().values());
        }

        @Override
        protected List<Ingredient> getIngredients(AlchemyRecipe recipe) {
            // TODO: Find this conflict here that is causing some items to not have EMC values
            if (recipe instanceof CatylistRecipe catylistRecipe) {
                return Arrays.asList(Ingredient.of(catylistRecipe.getInput().stream().map(ItemStack::new)), Ingredient.of(catylistRecipe.getCatylist().stream().map(ItemStack::new)));
            } else if (recipe instanceof TransmuteRecipe) {
                List<Item> results = recipe.getResults();
                results.remove(results.size() - 1);
                return Collections.singletonList(Ingredient.of(results.toArray(new Item[]{})));
            }
            PEIntegration.LOGGER.error("(Blue Skies) | Unsupported AlchemyRecipe: {}", recipe);
            return null;
        }

        @Override
        protected ItemStack getResult(AlchemyRecipe recipe) {
            if (recipe instanceof CatylistRecipe) {
                return new ItemStack(recipe.getResults().get(0));
            } else if (recipe instanceof TransmuteRecipe) {
                return new ItemStack(recipe.getResults().get(recipe.getResults().size() - 1));
            }
            PEIntegration.LOGGER.error("(Blue Skies) | Unsupported AlchemyRecipe: {}", recipe);
            return null;
        }
    }

    // TODO: Structure Gel API is giving config errors during data gen, disabled for now.
    /*
    @ConversionProvider(MODID)
    public static class BSConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Blue Skies")
                    .before(SkiesItems.horizofin_tunid, 64)
                    .before(SkiesItems.charscale_moki, 64)
                    .before(SkiesItems.municipal_monkfish, 64)
                    .before(SkiesItems.grittle_flatfish, 64)
                    .before(SkiesItems.monitor_tail, 64)
                    .before(SkiesItems.venison, 64)
                    .before(SkiesItems.carabeef, 64)
                    .before(SkiesItems.black_brewberry, 32)
                    .before(SkiesItems.pink_brewberry, 32)
                    .before(SkiesItems.brewberry, 32)
                    .before(SkiesItems.crescent_fruit, 32)
                    .before(SkiesItems.cherry, 16)
                    .before(SkiesItems.raw_charoite, 128)
                    .before(SkiesItems.raw_aquite, 128)
                    .before(SkiesItems.raw_falsite, 128)
                    .before(SkiesItems.raw_horizonite, 128)
                    .before(SkiesItems.raw_ventium, 128)
                    .before(SkiesItems.diopside_gem, 96)
                    .before(SkiesItems.aquatic_arc, 96)
                    .before(SkiesItems.pyrope_gem, 96)
                    .before(SkiesItems.moonstone_shard, 96)
                    .before(SkiesItems.pearl, 128)
                    .before(SkiesItems.soul_fragment, 156)
                    .before(SkiesItems.bug_guts, 1)
                    .before(SkiesItems.fox_pelt, 8)
                    .before(SkiesItems.azulfo_horn, 24)
                    .before(SkiesBlocks.snowcap_pinhead, 1);
        }
    }
     */
}
