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

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEFluids;
import blusunrize.immersiveengineering.common.register.IEItems;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmersiveEngineeringAddon {
    public static final String MODID = "immersiveengineering";

    protected static String NAME(String name) {
        return "ImmersiveEngineering" + name + "Mapper";
    }

    public static abstract class IEMultiblockRecipeMapper<R extends MultiblockRecipe> extends ARecipeTypeMapper<R> {
        @Override
        public NSSInput getInput(R recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            if (recipe.getFluidInputs() != null && recipe.getFluidInputs().isEmpty() && recipe.getItemInputs().isEmpty())
                PEIntegration.LOGGER.warn("Immersive Engineering Recipe ({}) contains no inputs!", recipeID);

            for (IngredientWithSize ingredient : recipe.getItemInputs()) {
                convertIngredient(ingredient.getCount(), ingredient.getBaseIngredient(), ingMap, fakeGroupData);
            }

            if (recipe.getFluidInputs() != null) {
                for (SizedFluidIngredient fluidInput : recipe.getFluidInputs()) {
                    convertFluidIngredient(fluidInput.amount(), Arrays.asList(fluidInput.getFluids()), ingMap, fakeGroupData);
                }
            }

            return new NSSInput(ingMap, fakeGroupData, true);
        }

        @Override
        public NSSOutput getOutput(R recipe) {
            ArrayList<Object> outputs = new ArrayList<>(recipe.getItemOutputs());

            if (recipe.getFluidOutputs() != null && !recipe.getFluidOutputs().isEmpty())
                outputs.addAll(recipe.getFluidOutputs());

            PEIntegration.LOGGER.info("Multiblock Recipe ({}) has outputs {}", recipeID, outputs);

            return mapOutputs(outputs.toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEAlloyMapper extends ARecipeTypeMapper<AlloyRecipe> {
        @Override
        public String getName() {
            return NAME("Alloy");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.ALLOY.get();
        }

        @Override
        public NSSInput getInput(AlloyRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            convertIngredient(recipe.input0.getCount(), recipe.input0.getBaseIngredient(), ingMap, fakeGroupData);
            convertIngredient(recipe.input1.getCount(), recipe.input1.getBaseIngredient(), ingMap, fakeGroupData);

            return new NSSInput(ingMap, fakeGroupData, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEArcFurnaceMapper extends IEMultiblockRecipeMapper<ArcFurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("ArcFurnace");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.ARC_FURNACE.get();
        }

        @Override
        public boolean convertRecipe(ArcFurnaceRecipe recipe) {
            if (recipe.isSpecialType(ArcRecyclingRecipe.SPECIAL_TYPE)) {
                PEIntegration.debugLog("Skipping Arc Furnace - Recycling Recipe ({})", recipeID);
                return false;
            }

            return super.convertRecipe(recipe);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEBlastFurnaceMapper extends ARecipeTypeMapper<BlastFurnaceRecipe> {
        @Override
        public String getName() {
            return NAME("BlastFurnace");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.BLAST_FURNACE.get();
        }

        @Override
        public NSSInput getInput(BlastFurnaceRecipe recipe) {
            return convertSingleIngredient(recipe.input.getCount(), recipe.input.getBaseIngredient());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEBlueprintMapper extends IEMultiblockRecipeMapper<BlueprintCraftingRecipe> {
        @Override
        public String getName() {
            return NAME("Blueprint");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.BLUEPRINT.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEBottlerMapper extends IEMultiblockRecipeMapper<BottlingMachineRecipe> {
        @Override
        public String getName() {
            return NAME("Bottling");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.BOTTLING_MACHINE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEClocheMapper extends ARecipeTypeMapper<ClocheRecipe> {
        @Override
        public String getName() {
            return NAME("Cloche");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Disabled by default because its plants";
        }

        @Override
        public boolean isAvailable() {
            return false; // Disabled by default
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.CLOCHE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IECokeOvenMapper extends ARecipeTypeMapper<CokeOvenRecipe> {
        @Override
        public String getName() {
            return NAME("CokeOven");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.COKE_OVEN.get();
        }

        @Override
        public NSSInput getInput(CokeOvenRecipe recipe) {
            return convertSingleIngredient(recipe.input.getCount(), recipe.input.getBaseIngredient());
        }

        //@Override
        //public NSSOutput getOutput(CokeOvenRecipe recipe) {
        //    if (recipe.creosoteOutput > 0) {
        //        ItemStack itemOutput = recipe.getResultItem(registryAccess).copy();
        //        FluidStack creosoteOutput = new FluidStack(IEFluids.CREOSOTE.getStill(), recipe.creosoteOutput);
//
        //        return mapOutputs(itemOutput, creosoteOutput);
        //    }
//
        //    return new NSSOutput(recipe.getResultItem(registryAccess));
        //}
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IECrusherMapper extends IEMultiblockRecipeMapper<CrusherRecipe> {
        @Override
        public String getName() {
            return NAME("Crusher");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.CRUSHER.get();
        }

        @Override
        public NSSOutput getOutput(CrusherRecipe recipe) {
            return new NSSOutput(recipe.output.get());
        }

        @Override
        protected List<Ingredient> getIngredients(CrusherRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEFermenterMapper extends ARecipeTypeMapper<FermenterRecipe> {
        @Override
        public String getName() {
            return NAME("Fermenter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.FERMENTER.get();
        }

        @Override
        public NSSOutput getOutput(FermenterRecipe recipe) {
            // We skip the itemOutput because it's just a glass bottle
            //return mapOutputs(recipe.itemOutput.get(), recipe.fluidOutput);
            return new NSSOutput(recipe.fluidOutput);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEMetalPressMapper extends ARecipeTypeMapper<MetalPressRecipe> {
        @Override
        public String getName() {
            return NAME("MetalPress");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.METAL_PRESS.get();
        }

        @Override
        protected List<Ingredient> getIngredients(MetalPressRecipe recipe) {
            return Collections.singletonList(recipe.input.getBaseIngredient());
        }

        @Override
        public NSSOutput getOutput(MetalPressRecipe recipe) {
            return new NSSOutput(recipe.output.get());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IEMixerMapper extends IEMultiblockRecipeMapper<MixerRecipe> {
        @Override
        public String getName() {
            return NAME("Mixer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.MIXER.get();
        }

        @Override
        public NSSOutput getOutput(MixerRecipe recipe) {
            return new NSSOutput(recipe.fluidOutput.copy());
        }

        @Override
        public NSSInput getInput(MixerRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            convertFluidIngredient(recipe.fluidInput.amount(), Arrays.asList(recipe.fluidInput.getFluids()), ingredientMap, fakeGroupMap);
            for (IngredientWithSize input : recipe.itemInputs) {
                convertIngredient(input.getCount(), input.getBaseIngredient(), ingredientMap, fakeGroupMap);
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IERefineryMapper extends IEMultiblockRecipeMapper<RefineryRecipe> {
        @Override
        public String getName() {
            return NAME("Refinery");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.REFINERY.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IESawmillMapper extends IEMultiblockRecipeMapper<SawmillRecipe> {
        @Override
        public String getName() {
            return NAME("Sawmill");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.SAWMILL.get();
        }

        @Override
        protected List<Ingredient> getIngredients(SawmillRecipe recipe) {
            return Collections.singletonList(recipe.input);
        }

        @Override
        public NSSOutput getOutput(SawmillRecipe recipe) {
            // TODO: Add support for multiple outputs
            return new NSSOutput(recipe.output.get());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class IESqueezerMapper extends IEMultiblockRecipeMapper<SqueezerRecipe> {
        @Override
        public String getName() {
            return NAME("Squeezer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == IERecipeTypes.SQUEEZER.get();
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

    @ConversionProvider(MODID)
    public static class IEConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for immersive engineering")
                    .before(commonTag("fiber_hemp"), 4)
                    .before(dustTag("wood"), 1)
                    .before(dustTag("sulfur"), 8)
                    .before(dustTag("nitrate"), 8)
                    .before(ingotTag("hop_graphite"), 12)
                    .before(IEItems.Ingredients.SLAG.get(), 8)
                    .before(IEFluids.CREOSOTE.getStill(), 1);

            // TODO: Replace this forEach. It should not be done this way.
            for (IEBlocks.BlockEntry<IEBaseBlock> block : IEBlocks.WoodenDecoration.TREATED_WOOD.values()) {
                builder.conversion(block).ingredient(ItemTags.PLANKS, 8).end();
            }
        }
    }
}
