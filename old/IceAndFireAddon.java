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

import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import com.google.common.collect.ImmutableList;
import com.tagnumelite.projecteintegration.api.recipe.ACustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.CustomRecipeMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.List;

public class IceAndFireAddon {
    public static final String MODID = "iceandfire";

    @CustomRecipeMapper(MODID)
    public static class IAFDragonForgeMapper extends ACustomRecipeMapper<DragonForgeRecipe> {
        @Override
        public String getName() {
            return "IceAndFireDragonForgeMapper";
        }

        @Override
        public String getDescription() {
            return "Recipe mapper for Ice and Fire DragonForge recipes";
        }

        @Override
        public List<DragonForgeRecipe> getRecipes() {
            return ImmutableList.copyOf(IafRecipeRegistry.ALL_FORGE_RECIPES);
        }

        @Override
        protected List<Ingredient> getIngredients(DragonForgeRecipe recipe) {
            return Arrays.asList(recipe.getInput(), recipe.getBlood());
        }

        @Override
        protected ItemStack getResult(DragonForgeRecipe recipe) {
            return recipe.getOutput();
        }
    }

    /* TODO: Ice and Fire doesn't like to be run in an dev enviroment.
    @ConversionProvider(MODID)
    public static class IAFDataGenerator extends AConversionProvider {
        protected static NormalizedSimpleStack bonesTag(String tag) {
            return forgeTag("bones/" + tag);
        }

        protected static NormalizedSimpleStack scaleTag(String tag) {
            return forgeTag("scales/" + tag);
        }

        protected static NormalizedSimpleStack iafTag(String tag) {
            return tag(MODID + ":" + tag);
        }

        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Ice and Fire: Dragons")
                    .before(gemTag("amethyst"), 2048)
                    .before(forgeTag("heart"), 262144)
                    .before(iafTag("mob_skulls"), 320)
                    .before(iafTag("dragon_skulls"), 624)
                    .before(iafTag("myrmex_harvestables"), 32)
                    .before(scaleTag("sea_serpent"), 256)
                    .before(scaleTag("dragon"), 512)
                    .before(bonesTag("dragon"), 156)
                    .before(bonesTag("wither"), 156)
                    .before(IafItemRegistry.ICE_DRAGON_BLOOD, 256)
                    .before(IafItemRegistry.LIGHTNING_DRAGON_BLOOD, 256)
                    .before(IafItemRegistry.FIRE_DRAGON_BLOOD, 256)
                    .before(IafItemRegistry.DREAD_SHARD, 144)
                    .before(IafItemRegistry.HIPPOGRYPH_TALON, 128)
                    .before(IafItemRegistry.HIPPOCAMPUS_FIN, 512)
                    .before(IafItemRegistry.SHINY_SCALES, 512)
                    .before(IafItemRegistry.SIREN_TEAR, 768)
                    .before(IafItemRegistry.CYCLOPS_EYE, 96)
                    .before(IafItemRegistry.PIXIE_DUST, 1)
                    .before(IafItemRegistry.PIXIE_WINGS, 1);
        }
    }
    */
}
