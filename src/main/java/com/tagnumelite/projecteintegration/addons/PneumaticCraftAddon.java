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

import com.mojang.datafixers.util.Either;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.desht.pneumaticcraft.api.crafting.AmadronTradeResource;
import me.desht.pneumaticcraft.api.crafting.ingredient.FluidContainerIngredient;
import me.desht.pneumaticcraft.api.crafting.recipe.*;
import me.desht.pneumaticcraft.common.registry.ModFluids;
import me.desht.pneumaticcraft.common.registry.ModItems;
import me.desht.pneumaticcraft.common.registry.ModRecipeTypes;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

public class PneumaticCraftAddon {
    public static final String MODID = "pneumaticcraft";

    static String NAME(String name) {
        return "PneumaticCraft" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRAmadronRecipeMapper extends ARecipeTypeMapper<AmadronRecipe> {
        @Override
        public String getName() {
            return NAME("AmadronRecipe");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.AMADRON.get();
        }

        @Override
        public NSSOutput getOutput(AmadronRecipe recipe) {
            AmadronTradeResource output = recipe.getOutput();
            if (output.isEmpty()) return NSSOutput.EMPTY;

            return output.resource().map(NSSOutput::new, NSSOutput::new);
        }

        @Override
        public NSSInput getInput(AmadronRecipe recipe) {
            AmadronTradeResource input = recipe.getInput();
            if (input.isEmpty()) return null;

            return input.resource().map(this::convertSingleItemStack, this::convertSingleFluidStack);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRAssemblyMapper extends ARecipeTypeMapper<AssemblyRecipe> {
        @Override
        public String getName() {
            return NAME("Assembly");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.ASSEMBLY_DRILL.get() ||
                    recipeType == ModRecipeTypes.ASSEMBLY_LASER.get() ||
                    recipeType == ModRecipeTypes.ASSEMBLY_DRILL_LASER.get();
        }

        @Override
        public NSSInput getInput(AssemblyRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            convertIngredient(recipe.getInputAmount(), recipe.getInput().ingredient(), ingredientMap, fakeGroupMap);

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        public NSSOutput getOutput(AssemblyRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRExplosionCraftingMapper extends ARecipeTypeMapper<ExplosionCraftingRecipe> {
        @Override
        public String getName() {
            return NAME("ExplosionCrafting");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Disabled by default because this mapper ignore loss rate.";
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.EXPLOSION_CRAFTING.get();
        }

        @Override
        public NSSInput getInput(ExplosionCraftingRecipe recipe) {
            return convertSingleIngredient(recipe.getInput().count(), recipe.getInput().ingredient());
        }

        @Override
        public NSSOutput getOutput(ExplosionCraftingRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRHeatFrameCoolingMapper extends ARecipeTypeMapper<HeatFrameCoolingRecipe> {
        @Override
        public String getName() {
            return NAME("HeatFrameCooling");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.HEAT_FRAME_COOLING.get();
        }

        @Override
        public NSSInput getInput(HeatFrameCoolingRecipe recipe) {
            Either<Ingredient, FluidContainerIngredient> eitherInput = recipe.getInput();

            if (eitherInput.right().isPresent()) {
                Either<FluidStack, FluidContainerIngredient.TagWithAmount> fluidIng = eitherInput.right().get().either();

                if (fluidIng.right().isPresent()) { // TODO: WTF, this is a mess. I must clean this up later
                    return convertSingleIngredient(eitherInput.right().get().amount(),
                            StreamSupport.stream(BuiltInRegistries.FLUID.getTagOrEmpty(fluidIng.right().get().tag()).spliterator(), false)
                                    .map(i -> new FluidStack(i.value(), 1)).toList());
                } else if (fluidIng.left().isPresent()) {
                    return convertSingleIngredient(1, Collections.singletonList(fluidIng.left().get()));
                }

            } else if (eitherInput.left().isPresent()) {
                return convertSingleIngredient(1, eitherInput.left().get());
            }

            return null;
        }

        @Override
        public NSSOutput getOutput(HeatFrameCoolingRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRPressureChamberMapper extends ARecipeTypeMapper<PressureChamberRecipe> {
        @Override
        public String getName() {
            return NAME("PressureChamber");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.PRESSURE_CHAMBER.get();
        }

        @Override
        public NSSInput getInput(PressureChamberRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            for (SizedIngredient ingredient : recipe.getInputs()) {
                convertIngredient(ingredient.count(), ingredient.ingredient(), ingMap, fakeGroupData);
            }

            return new NSSInput(ingMap, fakeGroupData, true);
        }

        @Override
        public NSSOutput getOutput(PressureChamberRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRRefineryMapper extends ARecipeTypeMapper<RefineryRecipe> {
        @Override
        public String getName() {
            return NAME("Refinery");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.REFINERY.get();
        }

        @Override
        public NSSInput getInput(RefineryRecipe recipe) {
            return convertSingleIngredient(recipe.getInput().amount(), Arrays.asList(recipe.getInput().ingredient().getStacks()));
        }

        @Override
        public NSSOutput getOutput(RefineryRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRThermoPlantMapper extends ARecipeTypeMapper<ThermoPlantRecipe> {
        @Override
        public String getName() {
            return NAME("ThermopneumaticProcessingPlant");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.THERMO_PLANT.get();
        }

        @Override
        public NSSOutput getOutput(ThermoPlantRecipe recipe) {
            ItemStack outputItem = recipe.getOutputItem();
            FluidStack outputFluid = recipe.getOutputFluid();
            boolean itemEmpty = outputItem == null || outputItem.isEmpty();
            boolean fluidEmpty = outputFluid == null || outputFluid.isEmpty();
            if (itemEmpty && fluidEmpty) return NSSOutput.EMPTY;

            if (fluidEmpty) {
                return new NSSOutput(outputItem);
            } else if (itemEmpty) {
                return new NSSOutput(outputFluid);
            } else {
                return mapOutputs(outputItem, outputFluid);
            }
        }

        @Override
        public NSSInput getInput(ThermoPlantRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            recipe.getInputItem().ifPresent(ingredient -> convertIngredient(ingredient, ingMap, fakeGroupData));
            recipe.getInputFluid().ifPresent(ingredient -> convertFluidIngredient(ingredient.amount(), Arrays.asList(ingredient.getFluids()), ingMap, fakeGroupData));

            return new NSSInput(ingMap, fakeGroupData, recipe.getInputFluid().isPresent() || recipe.getInputItem().isPresent());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRFluidMixerMapper extends ARecipeTypeMapper<FluidMixerRecipe> {
        @Override
        public String getName() {
            return NAME("FluidMixer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.FLUID_MIXER.get();
        }

        @Override
        public NSSOutput getOutput(FluidMixerRecipe recipe) {
            return mapOutputs(recipe.getOutputFluid(), recipe.getOutputItem());
        }

        @Override
        public NSSInput getInput(FluidMixerRecipe recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupData = new ArrayList<>();

            convertFluidIngredient(recipe.getInput1().amount(), Arrays.asList(recipe.getInput1().getFluids()), ingMap, fakeGroupData);
            convertFluidIngredient(recipe.getInput2().amount(), Arrays.asList(recipe.getInput2().getFluids()), ingMap, fakeGroupData);

            return new NSSInput(ingMap, fakeGroupData, true);
        }
    }

    @ConversionProvider(MODID)
    public static class PCRConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for PneumaticCraft")
                    .before(ModFluids.OIL.get(), 1)
                    .conversion(ModItems.PLASTIC).ingredient(ModFluids.PLASTIC.get(), 1000);
        }
    }
}
