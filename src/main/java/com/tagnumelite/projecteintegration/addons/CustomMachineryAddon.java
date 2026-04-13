/*
 * Copyright (c) 2019-2026 TagnumElite
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

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import fr.frinn.custommachinery.api.crafting.IMachineRecipe;
import fr.frinn.custommachinery.api.requirement.IRequirement;
import fr.frinn.custommachinery.api.requirement.RecipeRequirement;
import fr.frinn.custommachinery.api.requirement.RequirementIOMode;
import fr.frinn.custommachinery.common.crafting.craft.CustomCraftRecipe;
import fr.frinn.custommachinery.common.crafting.machine.CustomMachineRecipe;
import fr.frinn.custommachinery.common.init.Registration;
import fr.frinn.custommachinery.common.requirement.*;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class CustomMachineryAddon {
    public static final String MODID = "custommachinery";

    public static String NAME(String name) {
        return "CustomMachinery" + name + "Mapper";
    }

    public abstract static class AMachineRecipeMapper<R extends Recipe<?> & IMachineRecipe> extends ARecipeTypeMapper<R> {
        public static final List<Class<? extends IRequirement<?>>> SKIPPED_REQUIREMENTS = List.of(BiomeRequirement.class,
                ButtonRequirement.class, ChunkloadRequirement.class, DimensionRequirement.class, DropRequirement.class,
                DurabilityRequirement.class, EnergyRequirement.class, EnergyPerTickRequirement.class, FuelRequirement.class,
                CommandRequirement.class, PositionRequirement.class, RedstoneRequirement.class, SkyRequirement.class,
                TimeRequirement.class, WeatherRequirement.class, WorkingCoreRequirement.class, SpeedRequirement.class,
                LightRequirement.class, FunctionRequirement.class, EntityRequirement.class, StructureRequirement.class);

        @Override
        public boolean convertRecipe(R recipe) {
            NSSInput.Builder inputBuilder = getInputBuilder();
            NSSOutput.Builder outputBuilder = getOutputBuilder();

            for (RecipeRequirement<?, ?> recipeRequirement : recipe.getRequirements()) {
                if (recipeRequirement.chance() < 1.0D) continue; // Skip chanced outputs
                if (SKIPPED_REQUIREMENTS.contains(recipeRequirement.getClass())) continue; // Skipped filtered requirements

                IRequirement<?> requirement = recipeRequirement.requirement();
                boolean isInput = requirement.getMode() == RequirementIOMode.INPUT;

                switch(requirement) { // TODO: BlockRequirement & ExperienceRequirements & ItemFilter & ItemTransform
                    case ItemRequirement itemRequirement:
                        if (isInput) inputBuilder.addSizedIngredient(itemRequirement.ingredient());
                        else outputBuilder.addItem(itemRequirement.ingredient().getItems()[0]);
                        break;
                    case FluidRequirement fluidRequirement:
                        if (isInput) inputBuilder.addFluid(fluidRequirement.ingredient());
                        else outputBuilder.addFluid(fluidRequirement.ingredient().ingredient().getStacks()[0]);
                        break;
                    case FluidPerTickRequirement fluidPerTickRequirement:
                        // TODO: Double check this math
                        int amount = recipe.getRecipeTime() * fluidPerTickRequirement.ingredient().amount();
                        if (isInput) inputBuilder.addFluid(amount, fluidPerTickRequirement.ingredient().ingredient());
                        else outputBuilder.addFluid(amount, fluidPerTickRequirement.ingredient().ingredient().getStacks()[0].getFluid());
                        break;
                    default:
                        PEIntegration.LOGGER.warn("Unhandled machine ({}) requirement ({}) in recipe ({}), can be potentially ignored", requirement.getMode(), requirement, recipeID);
                }
            }

            NSSInput input = inputBuilder.build();
            if (input == null || !input.successful()) {
                if (input != null) PEIntegration.LOGGER.warn("CustomMachinery Recipe ({}) has failed NSSInput ({}), this can potentially be ignored", recipeID, input);
                return addConversionsAndReturn(input != null ? input.fakeGroupMap() : null, false);
            }

            NSSOutput output = outputBuilder.build();
            if (output == null || output.isEmpty()) {
                PEIntegration.debugLog("CustomMachinery Recipe ({}) contains no outputs: {}, this can potentially be ignored", recipeID, output);
                return false;
            }

            mapper.addConversion(output.amount, output.nss, input.getMap());
            return addConversionsAndReturn(input.fakeGroupMap(), true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CMMachineMapper extends AMachineRecipeMapper<CustomMachineRecipe> {
        @Override
        public String getName() {
            return NAME("Machine");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == Registration.CUSTOM_MACHINE_RECIPE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CMCraftMapper extends AMachineRecipeMapper<CustomCraftRecipe> {
        @Override
        public String getName() {
            return NAME("Craft");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == Registration.CUSTOM_CRAFT_RECIPE.get();
        }
    }
}
