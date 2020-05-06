/*
 * Copyright (c) 2019-2020 TagnumElite
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
package com.tagnumelite.projecteintegration.plugins.crafting;

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.RecipeRegistry;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementFluid;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RegistriesMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;

@PEIPlugin("modularmachinery")
public class PluginModularMachinery extends APEIPlugin {
    @Override
    public void setup() {
        PEIntegration.LOG.info("Requirement Types: {}", RegistriesMM.REQUIREMENT_TYPE_REGISTRY.getEntries());
        for (DynamicMachine machine : MachineRegistry.getRegistry()) {
            addMapper(new MachineMapper(machine));
        }
    }

    private static class MachineMapper extends PEIMapper {
        private final DynamicMachine machine;

        public MachineMapper(DynamicMachine machine) {
            super(machine.getLocalizedName());
            this.machine = machine;
        }

        @Override
        public void setup() {
            for (MachineRecipe recipe : RecipeRegistry.getRegistry().getRecipesFor(machine)) {
                IngredientMap<Object> ingredients = new IngredientMap<>();
                ArrayList<ItemStack> item_outputs = new ArrayList<>();
                ArrayList<FluidStack> fluid_outputs = new ArrayList<>();

                for (ComponentRequirement<?, ?> requirement : recipe.getCraftingRequirements()) {
                    if (requirement instanceof RequirementItem) {
                        RequirementItem item_req = (RequirementItem) requirement;
                        // We don't do chanced outputs/inputs
                        if (item_req.chance < 1.0) continue;
                        // We also don't count fuel
                        if (item_req.requirementType == RequirementItem.ItemRequirementType.FUEL) continue;

                        switch (requirement.getActionType()) {
                            case INPUT:
                                if (item_req.requirementType == RequirementItem.ItemRequirementType.ITEMSTACKS) {
                                    ingredients.addIngredient(item_req.required, item_req.required.getCount());
                                } else if (item_req.requirementType == RequirementItem.ItemRequirementType.OREDICT) {
                                    ingredients.addIngredient(item_req.oreDictName, item_req.oreDictItemAmount);
                                }
                                break;
                            case OUTPUT:
                                // We don't do OreDict outputs
                                if (item_req.requirementType == RequirementItem.ItemRequirementType.OREDICT) continue;
                                item_outputs.add(item_req.required);
                                break;
                        }
                    } else if (requirement instanceof RequirementFluid) {
                        RequirementFluid fluid_req = (RequirementFluid) requirement;
                        if (fluid_req.chance < 1.0) continue;

                        switch (requirement.getActionType()) {
                            case INPUT:
                                ingredients.addIngredient(fluid_req.required.asFluidStack(), fluid_req.required.getAmount());
                                break;
                            case OUTPUT:
                                fluid_outputs.add(fluid_req.required.asFluidStack());
                                break;
                        }
                    } //Todo: else if (requirement instanceof RequirementGas) {}
                }

                Map<Object, Integer> map = ingredients.getMap();

                ArrayList<Object> outputs = new ArrayList<>();
                outputs.addAll(item_outputs);
                outputs.addAll(fluid_outputs);
                addConversion(outputs, map);
            }
        }
    }
}
