package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.Map;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirements.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirements.RequirementFluid;
import hellfirepvp.modularmachinery.common.crafting.requirements.RequirementItem;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@RegPEIPlugin(modid = "modularmachinery")
public class PluginModularMachinery extends PEIPlugin {
	public PluginModularMachinery(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (DynamicMachine machine : MachineRegistry.getRegistry()) {
			addMapper(new MachineMapper(machine));
		}
	}

	private class MachineMapper extends PEIMapper {
		private final DynamicMachine machine;

		public MachineMapper(DynamicMachine machine) {
			super(machine.getLocalizedName());
			this.machine = machine;
		}

		@Override
		public void setup() {
			for (MachineRecipe recipe : machine.getAvailableRecipes()) {
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				ArrayList<ItemStack> item_outputs = new ArrayList<ItemStack>();
				ArrayList<FluidStack> fluid_outputs = new ArrayList<FluidStack>();

				for (ComponentRequirement<?> req : recipe.getCraftingRequirements()) {
					if (req instanceof RequirementEnergy) {
						continue;
					} else if (req instanceof RequirementItem) {
						RequirementItem req_t = (RequirementItem) req;
						switch (req_t.getActionType()) {
						case INPUT:
							if (req_t.requirementType == RequirementItem.ItemRequirementType.ITEMSTACKS) {
								if (req_t.required == null || req_t.required == ItemStack.EMPTY)
									continue;

								ingredients.addIngredient(req_t.required, req_t.required.getCount());
							} else if (req_t.requirementType == RequirementItem.ItemRequirementType.OREDICT) {
								if (req_t.oreDictName == null || req_t.oreDictName == ""
										|| req_t.oreDictItemAmount <= 0)
									continue;

								ingredients.addIngredient(req_t.oreDictName, req_t.oreDictItemAmount);
							}
							break;

						case OUTPUT:
							if (req_t.requirementType == RequirementItem.ItemRequirementType.ITEMSTACKS) {
								if (req_t.required == null || req_t.required == ItemStack.EMPTY)
									continue;

								item_outputs.add(req_t.required);
							} // We don't do Ore Dict outputs
							break;
						}
					} else if (req instanceof RequirementFluid) {
						RequirementFluid req_f = (RequirementFluid) req;
						FluidStack fluid = req_f.required.asFluidStack();
						
						switch (req_f.getActionType()) {
						case INPUT:
							if (fluid == null || fluid.amount <= 0)
								continue;
							
							ingredients.addIngredient(fluid, fluid.amount);
							break;
						
						case OUTPUT:
							if (fluid == null || fluid.amount <= 0)
								continue;
							
							fluid_outputs.add(fluid);
							break;
						}
					}
				}
				
				Map<Object, Integer> map = ingredients.getMap();
				
				ArrayList<Object> outputs = new ArrayList<>();
				outputs.addAll(item_outputs);
				outputs.addAll(fluid_outputs);
				addConversion(outputs, map);
				/*
				for (ItemStack output : item_outputs) {
					addConversion(output, map);
				}
				
				for (FluidStack output : fluid_outputs) {
					addConversion(output, map);
				}*/
			}
		}
	}
}
