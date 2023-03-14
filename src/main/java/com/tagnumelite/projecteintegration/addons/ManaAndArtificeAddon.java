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

import com.ma.api.recipes.IItemAndPatternRecipe;
import com.ma.api.rituals.IRitualReagent;
import com.ma.items.ItemInit;
import com.ma.recipes.RecipeInit;
import com.ma.recipes.arcanefurnace.ArcaneFurnaceRecipe;
import com.ma.recipes.eldrin.EldrinAltarRecipe;
import com.ma.recipes.manaweaving.ManaweavingRecipe;
import com.ma.recipes.rituals.RitualRecipe;
import com.ma.recipes.runeforging.RuneforgingRecipe;
import com.ma.recipes.runeforging.RunescribingRecipe;
import com.ma.tools.MATags;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManaAndArtificeAddon {
    public static final String MODID = "manaandartifice";

    public static String NAME(String name) {
        return "ManaAndArtifice" + name + "Mapper";
    }

    /*
    - Arcane Compound
    - Arcane Ash
    - Bone Ash
    - Vintuem Dust
     */

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAAArcaneFurnaceRecipeMapper extends ARecipeTypeMapper<ArcaneFurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("ArcaneFurnace");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.ARCANE_FURNACE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(ArcaneFurnaceRecipe recipe) {
            return Collections.singletonList(Ingredient.of(MATags.smartLookupItem(recipe.getInputItem()).stream().map(ItemStack::new)));
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAAEldrinAltarRecipeMapper extends MAAItemAndPatternRecipeMapper<EldrinAltarRecipe> {
        @Override
        public String getName() {
            return NAME("EldrinAltar");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.ELDRIN_ALTAR_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAAManaweavingRecipeMapper extends MAAItemAndPatternRecipeMapper<ManaweavingRecipe> {
        @Override
        public String getName() {
            return NAME("Manaweaving");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.MANAWEAVING_RECIPE_TYPE; // TODO: MANAWEAVING_PATTERN
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAARitualRecipeMapper extends ARecipeTypeMapper<RitualRecipe> {
        @Override
        public String getName() {
            return NAME("Ritual");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + "NOTE: Disabled by default because this really shouldn't be here.";
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.RITUAL_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(RitualRecipe recipe) {
            List<Ingredient> recipeIngredients = new ArrayList<>();
            IRitualReagent[][] reagents = recipe.getReagents();

            int j;
            for (IRitualReagent[] reagent : reagents) {
                for (j = 0; j < reagent.length; ++j) {
                    if (reagent[j] != null && !reagent[j].isEmpty() && !reagent[j].isDynamic()) {
                        recipeIngredients.add(Ingredient.of(MATags.smartLookupItem(reagent[j].getResourceLocation()).stream().map(ItemStack::new)));
                    }
                }
            }

            return recipeIngredients;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAARunescribingRecipeMapper extends ARecipeTypeMapper<RunescribingRecipe> {
        @Override
        public String getName() {
            return NAME("Runescribing");
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.RUNESCRIBING_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class MAARuneforgingRecipeMapper extends ARecipeTypeMapper<RuneforgingRecipe> {
        @Override
        public String getName() {
            return NAME("Runeforging");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeInit.RUNEFORGING_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(RuneforgingRecipe recipe) {
            List<Ingredient> inputs = new ArrayList<>();
            inputs.add(Ingredient.of(MATags.smartLookupItem(recipe.getMaterial()).stream().map(ItemStack::new)));
            inputs.add(Ingredient.of(MATags.smartLookupItem(recipe.getPatternResource()).stream().map(ItemStack::new)));
            return inputs;
        }
    }

    @ConversionProvider(MODID)
    public static class MAAConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Mana and Artifice")
                    .before(ItemInit.VINTEUM_DUST.get(), 32);
        }
    }

    public static abstract class MAAItemAndPatternRecipeMapper<R extends IItemAndPatternRecipe & IRecipe<?>> extends ARecipeTypeMapper<R> {
        @Override
        protected List<Ingredient> getIngredients(R recipe) {
            List<Ingredient> recipeIngredients = new ArrayList<>();
            ResourceLocation[] requiredItems = recipe.getRequiredItems();
            int requireItemsCount = requiredItems.length;

            int idx;
            ResourceLocation resourceLocation;
            for (idx = 0; idx < requireItemsCount; ++idx) {
                resourceLocation = requiredItems[idx];
                recipeIngredients.add(Ingredient.of(MATags.smartLookupItem(resourceLocation).stream().map((item) -> {
                    ItemStack stack = new ItemStack(item);
                    if (stack.getItem() == Items.POTION && stack.getTag() == null) {
                        PotionUtils.setPotion(stack, Potions.WATER);
                    }

                    return stack;
                })));
            }
            return recipeIngredients;
        }
    }
}
