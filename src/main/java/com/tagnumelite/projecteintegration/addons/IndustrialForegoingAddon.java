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

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.recipe.CrusherRecipe;
import com.buuz135.industrial.recipe.DissolutionChamberRecipe;
import com.buuz135.industrial.recipe.StoneWorkGenerateRecipe;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndustrialForegoingAddon {
    public static final String MODID = "industrialforegoing";

    public static String NAME(String name) {
        return "IndustrialForegoing" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IFCrusherMapper extends ARecipeTypeMapper<CrusherRecipe> {
        @Override
        public String getName() {
            return NAME("Crusher");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModuleCore.CRUSHER_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(CrusherRecipe recipe) {
            return mapOutput((Object[]) recipe.output.getItems());
        }

        @Override
        protected List<Ingredient> getIngredients(CrusherRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IFDissolutionChamberMapper extends ARecipeTypeMapper<DissolutionChamberRecipe> {
        @Override
        public String getName() {
            return NAME("DissolutionChamber");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModuleCore.DISSOLUTION_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(DissolutionChamberRecipe recipe) {
            if (recipe.output.isPresent() && recipe.outputFluid.isEmpty()) {
                return super.getOutput(recipe);
            }
            return mapOutputs(recipe.output, recipe.outputFluid);
        }

        @Override
        public NSSInput getInput(DissolutionChamberRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            for (Ingredient input : recipe.input) {
                convertIngredient(Ingredient.of(input.getItems()), ingMap, fakeGroupData);
            }

            if (recipe.inputFluid != null) {
                convertFluidIngredient(recipe.inputFluid.amount(), List.of(recipe.inputFluid.getFluids()), ingMap, fakeGroupData);
            }

            return new NSSInput(ingMap, fakeGroupData, true);
        }
    }

    // OTHERS IN BETWEEN

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IFStoneWorkGenerateMapper extends ARecipeTypeMapper<StoneWorkGenerateRecipe> {
        @Override
        public String getName() {
            return NAME("StoneWorksGenerate");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Disabled by default.";
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModuleCore.STONEWORK_GENERATE_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(StoneWorkGenerateRecipe recipe) {
            ItemStack output = recipe.output.copy();
            if (output.isEmpty()) return null;
            return new NSSOutput(output);
        }

        @Override
        public NSSInput getInput(StoneWorkGenerateRecipe recipe) {
            int lavaRequired = recipe.lavaConsume;
            int waterRequired = recipe.waterConsume;
            if (lavaRequired == 0 && waterRequired == 0) {
                return null;
            }

            Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();

            if (lavaRequired > 0)
                ingredientMap.put(NSSFluid.createFluid(Fluids.LAVA), lavaRequired);

            if (waterRequired > 0)
                ingredientMap.put(NSSFluid.createFluid(Fluids.WATER), waterRequired);

            return new NSSInput(ingredientMap, true);
        }
    }

    @ConversionProvider(MODID)
    public static class IFConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Sets default conversions for Industrial Foregoing")
                    .before(ModuleCore.SEWAGE.getSourceFluid().get(), 1)
                    .before(ModuleCore.DRY_RUBBER.get(), 12)
                    .before(ModuleCore.SLUDGE.getSourceFluid().get(), 1)
                    .before(ModuleCore.ESSENCE.getSourceFluid().get(), 1)
                    .before(ModuleCore.PINK_SLIME_ITEM.get(), 16)
                    .before(ModuleCore.ETHER.getSourceFluid().get(), 1)
                    .conversion(ModuleCore.LATEX.getSourceFluid().get(), 1600).ingredient(ItemTags.LOGS).end()
                    .conversion(ModuleCore.FERTILIZER.get()).ingredient(ModuleCore.SEWAGE.getSourceFluid().get(), 1000).end();
            //.conversion(ModuleCore.TINY_DRY_RUBBER).ingredient(Fluids.WATER, 500).ingredient(ModuleCore.LATEX.getSourceFluid(), 100).end();
        }
    }
}
