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

import com.tagnumelite.projecteintegration.api.recipe.APEIRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import me.desht.pneumaticcraft.api.crafting.ingredient.FluidIngredient;
import me.desht.pneumaticcraft.api.crafting.recipe.*;
import me.desht.pneumaticcraft.common.recipes.PneumaticCraftRecipeType;
import me.desht.pneumaticcraft.common.recipes.amadron.AmadronOffer;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class PneumaticCraftAddon {
    // TODO: PneumaticCraft implements a special FluidIngredient.
    // TODO: I noticed this way too late. Need to inspect the other recipes.
    public static final String MODID = "pneumaticcraft";

    static String NAME(String name) {
        return "PneumaticCraft"+name+"Mapper";
    }

    public static abstract class PCRMapper<R extends PneumaticCraftRecipe> extends APEIRecipeMapper<R> {
        @Override
        protected boolean convertIngredient(Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap, List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
            if (ingredient instanceof FluidIngredient) {
                return convertFluidIngredient((FluidIngredient) ingredient, ingredientMap, fakeGroupMap);
            } else {
                return super.convertIngredient(ingredient, ingredientMap, fakeGroupMap);
            }
        }

        protected boolean convertFluidIngredient(FluidIngredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap, List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
            List<FluidStack> matches = ingredient.getFluidStacks();
            if (matches == null) {
                return false;
            } else if (matches.size() == 1) {
                //Handle this ingredient as a direct representation of the stack it represents
                return !addIngredient(ingredientMap, matches.get(0));
            } else if (matches.size() > 0) {
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                List<FluidStack> stacks = new ArrayList<>();

                for (FluidStack match : matches) {
                    //Validate it is not an empty stack in case mods do weird things in custom ingredients
                    if (!match.isEmpty()) {
                        rawNSSMatches.add(NSSFluid.createFluid(match));
                        stacks.add(match);
                    }
                }

                int count = stacks.size();
                if (count == 1) {
                    return !addIngredient(ingredientMap, stacks.get(0));
                } else if (count > 1) {
                    //Handle this ingredient as the representation of all the stacks it supports
                    Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                    NormalizedSimpleStack dummy = group.getA();
                    ingredientMap.addIngredient(dummy, Math.max(ingredient.getAmount(), 1));
                    if (group.getB()) {
                        //Only lookup the matching stacks for the group with conversion if we don't already have
                        // a group created for this dummy ingredient
                        // Note: We soft ignore cases where it fails/there are no matching group ingredients
                        // as then our fake ingredient will never actually have an emc value assigned with it
                        // so the recipe won't either
                        List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                        for (FluidStack stack : stacks) {
                            IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                            if (addIngredient(groupIngredientMap, stack.copy())) {
                                return false;
                            }
                            groupIngredientMaps.add(groupIngredientMap);
                        }
                        fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                    }
                }
            }
            return true;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRAmadronOfferMapper extends APEIRecipeMapper<AmadronOffer> {
        @Override
        public String getName() {
            return NAME("AmadronOffer");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.AMADRON_OFFERS;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRAssemblyMapper extends APEIRecipeMapper<AssemblyRecipe> {
        @Override
        public String getName() {
            return NAME("Assembly");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.ASSEMBLY_DRILL ||
                    iRecipeType == PneumaticCraftRecipeType.ASSEMBLY_LASER ||
                    iRecipeType == PneumaticCraftRecipeType.ASSEMBLY_DRILL_LASER;
        }

        @Override
        protected NSSInput getInput(AssemblyRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            convertIngredient(recipe.getInputAmount(), recipe.getInput(), ingredientMap, fakeGroupMap);
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        protected NSSOutput getOutput(AssemblyRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRExplosionCraftingMapper extends APEIRecipeMapper<ExplosionCraftingRecipe> {
        @Override
        public String getName() {
            return NAME("ExplosionCrafting");
        }

        @Override
        public String getDescription() {
            return "Disabled by default because this mapper ignore loss rate.";
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.EXPLOSION_CRAFTING;
        }

        @Override
        protected List<Ingredient> getIngredients(ExplosionCraftingRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        protected NSSOutput getOutput(ExplosionCraftingRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRHeatFrameCoolingMapper extends PCRMapper<HeatFrameCoolingRecipe> {
        @Override
        public String getName() {
            return NAME("HeatFrameCooling");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.HEAT_FRAME_COOLING;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRPressureChamberMapper extends APEIRecipeMapper<PressureChamberRecipe> {
        @Override
        public String getName() {
            return NAME("PressureChamber");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.PRESSURE_CHAMBER;
        }

        @Override
        protected List<Ingredient> getIngredients(PressureChamberRecipe recipe) {
            return recipe.getInputsForDisplay();
        }

        @Override
        protected NSSOutput getOutput(PressureChamberRecipe recipe) {
            return mapOutputs(recipe.getResultsForDisplay().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRRefineryMapper extends PCRMapper<RefineryRecipe> {
        @Override
        public String getName() {
            return NAME("Refinery");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.REFINERY;
        }

        @Override
        protected NSSOutput getOutput(RefineryRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class PCRThermoPlantMapper extends PCRMapper<ThermoPlantRecipe> {
        @Override
        public String getName() {
            return NAME("ThermopneumaticProcessingPlant");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == PneumaticCraftRecipeType.THERMO_PLANT;
        }

        @Override
        protected NSSOutput getOutput(ThermoPlantRecipe recipe) {
            ItemStack outputItem = recipe.getOutputItem();
            FluidStack outputFluid = recipe.getOutputFluid();
            boolean itemEmpty = outputItem == null || outputItem.isEmpty();
            boolean fluidEmpty = outputFluid == null || outputFluid.isEmpty();
            if (itemEmpty && fluidEmpty) return NSSOutput.EMPTY;

            if (!itemEmpty && fluidEmpty) {
                return new NSSOutput(outputItem);
            } else if (itemEmpty/* && !fluidEmpty*/) {
                return new NSSOutput(outputFluid);
            } else {
                return mapOutputs(outputItem, outputFluid);
            }
        }

        @Override
        protected List<Ingredient> getIngredients(ThermoPlantRecipe recipe) {
            List<Ingredient> inputs = new ArrayList<>();

            if (!(recipe.getInputItem() == null || recipe.getInputItem().isEmpty())) {
                inputs.add(recipe.getInputItem());
            }

            if (!(recipe.getInputFluid() == null || recipe.getInputFluid().isEmpty())) {
                inputs.add(recipe.getInputFluid());
            }

            return inputs;
        }
    }

    //FLUID_MIXER
}
