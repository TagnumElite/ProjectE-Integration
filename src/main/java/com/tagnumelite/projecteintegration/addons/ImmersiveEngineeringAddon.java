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

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.common.IEContent;
import com.tagnumelite.projecteintegration.api.Utils;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class ImmersiveEngineeringAddon {
    public static final String MODID = "immersiveengineering";

    protected static String NAME(String name) {
        return "ImmersiveEngineering" + name + "Mapper";
    }

    protected static String DESC(String name) {
        return "Recipe mapper for Immersive Engineering " + name + " recipes";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEAlloyMapper extends ARecipeTypeMapper<AlloyRecipe> {
        @Override
        public String getName() {
            return NAME("Alloy");
        }

        @Override
        public String getDescription() {
            return DESC("Alloy");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AlloyRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(AlloyRecipe recipe) {
            return Arrays.asList(recipe.input0.getBaseIngredient(), recipe.input1.getBaseIngredient());
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEArcFurnaceMapper extends ARecipeTypeMapper<ArcFurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("ArcFurnace");
        }

        @Override
        public String getDescription() {
            return DESC("Arc Furnace");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ArcFurnaceRecipe.TYPE;
        }

        @Override
        public NSSOutput getOutput(ArcFurnaceRecipe recipe) {
            return super.getOutput(recipe);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEBlastFurnaceMapper extends ARecipeTypeMapper<BlastFurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("BlastFurnace");
        }

        @Override
        public String getDescription() {
            return DESC("Blast Furnace");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == BlastFurnaceRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(BlastFurnaceRecipe recipe) {
            return Collections.singletonList(recipe.input.getBaseIngredient());
        }

        @Override
        public NSSOutput getOutput(BlastFurnaceRecipe recipe) {
            ItemStack itemOutput = recipe.getResultItem().copy();
            ItemStack slagOutput = recipe.slag.copy();
            NormalizedSimpleStack itemStack = NSSItem.createItem(itemOutput);
            NormalizedSimpleStack slagStack = NSSItem.createItem(slagOutput);

            Tuple<NormalizedSimpleStack, Boolean> group = getFakeGroup(itemStack, slagStack);
            NormalizedSimpleStack dummy = group.getA();
            Map<NormalizedSimpleStack, Integer> dummyMap = Utils.getDummyMap(dummy);

            mapper.addConversion(slagOutput.getCount(), slagStack, dummyMap);
            mapper.addConversion(itemOutput.getCount(), itemStack, dummyMap);
            return new NSSOutput(2, dummy);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IECokeOvenMapper extends ARecipeTypeMapper<CokeOvenRecipe> {
        @Override
        public String getName() {
            return NAME("CokeOven");
        }

        @Override
        public String getDescription() {
            return DESC("Coke Oven");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == CokeOvenRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(CokeOvenRecipe recipe) {
            return Collections.singletonList(recipe.input.getBaseIngredient());
        }

        @Override
        public NSSOutput getOutput(CokeOvenRecipe recipe) {
            if (recipe.creosoteOutput > 0) {
                ItemStack itemOutput = recipe.getResultItem().copy();
                FluidStack creosoteOutput = new FluidStack(IEContent.fluidCreosote, recipe.creosoteOutput);
                NormalizedSimpleStack itemStack = NSSItem.createItem(itemOutput);
                NormalizedSimpleStack fluidStack = NSSFluid.createFluid(creosoteOutput);

                Tuple<NormalizedSimpleStack, Boolean> group = getFakeGroup(fluidStack, itemStack);
                NormalizedSimpleStack dummy = group.getA();
                Map<NormalizedSimpleStack, Integer> dummyMap = Utils.getDummyMap(dummy);

                mapper.addConversion(creosoteOutput.getAmount(), fluidStack, dummyMap);
                mapper.addConversion(itemOutput.getCount(), itemStack, dummyMap);
                return new NSSOutput(2, dummy);
            } else {
                return new NSSOutput(recipe.getResultItem());
            }
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IECrusherMapper extends ARecipeTypeMapper<CrusherRecipe> {
        @Override
        public String getName() {
            return NAME("Crusher");
        }

        @Override
        public String getDescription() {
            return DESC("Crusher");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == CrusherRecipe.TYPE;
        }

        @Override
        public NSSOutput getOutput(CrusherRecipe recipe) {
            return new NSSOutput(recipe.output.copy());
        }

        @Override
        protected List<Ingredient> getIngredients(CrusherRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEMetalPressMapper extends ARecipeTypeMapper<MetalPressRecipe> {
        @Override
        public String getName() {
            return NAME("MetalPress");
        }

        @Override
        public String getDescription() {
            return DESC("Metal Press");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == MetalPressRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(MetalPressRecipe recipe) {
            return Collections.singletonList(recipe.input.getBaseIngredient());
        }

        @Override
        public NSSOutput getOutput(MetalPressRecipe recipe) {
            return new NSSOutput(recipe.output.copy());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEMixerMapper extends ARecipeTypeMapper<MixerRecipe> {
        @Override
        public String getName() {
            return NAME("Mixer");
        }

        @Override
        public String getDescription() {
            return DESC("Mixer");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            //return iRecipeType == MixerRecipe.TYPE;
            return false;
        }

        @Override
        public NSSOutput getOutput(MixerRecipe recipe) {
            return new NSSOutput(recipe.fluidOutput.copy());
        }

        @Override
        public NSSInput getInput(MixerRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            //ingredientMap.addIngredient(recipe.fluidInput);
            // TODO: AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH

            for (IngredientWithSize input : recipe.itemInputs) {
                convertIngredient(input.getCount(), input.getBaseIngredient(), ingredientMap, fakeGroupMap);
            }

            return super.getInput(recipe);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IESawmillMapper extends ARecipeTypeMapper<SawmillRecipe> {
        @Override
        public String getName() {
            return NAME("Sawmill");
        }

        @Override
        public String getDescription() {
            return DESC("Sawmill");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == SawmillRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(SawmillRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }

        @Override
        public NSSOutput getOutput(SawmillRecipe recipe) {
            // TODO: Add support for multiple outputs
            return new NSSOutput(recipe.output.copy());
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IESqueezerMapper extends ARecipeTypeMapper<SqueezerRecipe> {
        @Override
        public String getName() {
            return NAME("Squeezer");
        }

        @Override
        public String getDescription() {
            return DESC("Squeezer");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == SqueezerRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(SqueezerRecipe recipe) {
            return Collections.singletonList(recipe.input.getBaseIngredient());
        }

        @Override
        public NSSOutput getOutput(SqueezerRecipe recipe) {
            return mapOutputs(recipe.itemOutput, recipe.fluidOutput);
        }
    }

        /*
        BlueprintCraftingRecipe
        BottlingMachineRecipe
        FermenterRecipe
        RefineryRecipe
        ClocheRecipe
        */
}
