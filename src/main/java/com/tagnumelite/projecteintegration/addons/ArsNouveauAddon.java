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

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArsNouveauAddon {
    public static final String MODID = "ars_nouveau";

    protected static String NAME(String name) {
        return "ArsNouveau" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ANCrushMapper extends ARecipeTypeMapper<CrushRecipe> {

        @Override
        public String getName() {
            return NAME("Crush");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.CRUSH_TYPE.get();
        }

        @Override
        protected List<Ingredient> getIngredients(CrushRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }

        @Override
        public NSSOutput getOutput(CrushRecipe recipe) {
            Object[] outputs = recipe.outputs.stream().filter(output -> output.chance > 1f).map(output -> output.stack).toArray();

            if (outputs.length == 0) return null;

            return mapOutputs(outputs);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ANEnchantingApparatusMapper extends ARecipeTypeMapper<EnchantingApparatusRecipe> {

        @Override
        public String getName() {
            return NAME("EnchantingApparatus");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.APPARATUS_TYPE.get();
        }

        @Override
        protected List<Ingredient> getIngredients(EnchantingApparatusRecipe recipe) {
            List<Ingredient> ingredients = new ArrayList<>(recipe.pedestalItems);
            ingredients.add(recipe.reagent);
            return ingredients;
        }

        @Override
        public NSSOutput getOutput(EnchantingApparatusRecipe recipe) {
            return new NSSOutput(recipe.result);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ANGlyphMapper extends ARecipeTypeMapper<GlyphRecipe> {
        @Override
        public String getName() {
            return NAME("Glyph");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.GLYPH_TYPE.get();
        }

        @Override
        protected List<Ingredient> getIngredients(GlyphRecipe recipe) {
            return recipe.inputs;
        }

        @Override
        public NSSOutput getOutput(GlyphRecipe recipe) {
            return new NSSOutput(recipe.output);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ANImbuementMapper extends ARecipeTypeMapper<ImbuementRecipe> {
        @Override
        public String getName() {
            return NAME("Imbuement");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeRegistry.IMBUEMENT_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(ImbuementRecipe recipe) {
            return new NSSOutput(recipe.output);
        }

        @Override
        protected List<Ingredient> getIngredients(ImbuementRecipe recipe) {
            List<Ingredient> ingredients = new ArrayList<>(recipe.pedestalItems);
            ingredients.add(recipe.input);
            return ingredients;
        }
    }

    @ConversionProvider(MODID)
    public static class ANConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Ars Nouveau")
                    .before(ItemsRegistry.EXPERIENCE_GEM, 16)
                    .before(ItemsRegistry.WILDEN_SPIKE, 64)
                    .before(ItemsRegistry.WILDEN_HORN, 96)
                    .before(ItemsRegistry.WIXIE_SHARD, 32)
                    .before(ItemsRegistry.WHIRLISPRIG_SHARDS, 16)
                    .before(ItemsRegistry.WILDEN_WING, 72)
                    .before(ItemsRegistry.WILDEN_TRIBUTE, 96)
                    .conversion(ItemsRegistry.MAGE_BLOOM).ingredient(BlockRegistry.MAGE_BLOOM_CROP).end()
                    .conversion(ItemsRegistry.DRYGMY_SHARD).ingredient(ItemsRegistry.WILDEN_HORN).end()
                    .conversion(ItemsRegistry.STARBUNCLE_SHARD).ingredient(Items.GOLD_NUGGET).end();
        }
    }
}
