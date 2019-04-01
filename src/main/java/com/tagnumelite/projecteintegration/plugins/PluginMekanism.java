/*
 * Copyright (c) 2019 TagnumElite
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
package com.tagnumelite.projecteintegration.plugins;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseObject;
import mekanism.api.infuse.InfuseRegistry;
import mekanism.api.infuse.InfuseType;
import mekanism.common.InfuseStorage;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.inputs.PressurizedInput;
import mekanism.common.recipe.machines.AdvancedMachineRecipe;
import mekanism.common.recipe.machines.BasicMachineRecipe;
import mekanism.common.recipe.machines.ChanceMachineRecipe;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;
import mekanism.common.recipe.machines.CrystallizerRecipe;
import mekanism.common.recipe.machines.DissolutionRecipe;
import mekanism.common.recipe.machines.DoubleMachineRecipe;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import mekanism.common.recipe.machines.OxidationRecipe;
import mekanism.common.recipe.machines.PressurizedRecipe;
import mekanism.common.recipe.machines.SeparatorRecipe;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import mekanism.common.recipe.machines.ThermalEvaporationRecipe;
import mekanism.common.recipe.machines.WasherRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import mekanism.common.recipe.outputs.PressurizedOutput;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@RegPEIPlugin(modid = "mekanism")
public class PluginMekanism extends PEIPlugin {
	private final Map<Gas, Object> GAS_MAP = new HashMap<Gas, Object>();
	private final Map<InfuseType, Object> INFUSE_MAP = new HashMap<InfuseType, Object>();

	public PluginMekanism(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (Gas gas : GasRegistry.getRegisteredGasses()) {
			Object obj = new Object();
			GAS_MAP.put(gas, obj);

			// For now, don't add EMC to gases. Gas should get EMC from inherited items
			// addEMC(gas.getName(), obj, 1, "EMC for Gas: " + gas.getName());

		}

		IConversionProxy proxy = ProjectEAPI.getConversionProxy();
		for (Entry<ItemStack, InfuseObject> entry : InfuseRegistry.getObjectMap().entrySet()) {
			ItemStack item = entry.getKey();
			InfuseObject infuse = entry.getValue();

			Object obj = new Object();
			if (INFUSE_MAP.containsKey(infuse.type))
				obj = INFUSE_MAP.get(infuse.type);
			else
				INFUSE_MAP.put(infuse.type, obj);

			proxy.addConversion(infuse.stored, obj, ImmutableMap.of(item, item.getCount()));
		}

		if (MachineType.ENRICHMENT_CHAMBER.isEnabled())
			addMapper(new BasicMachineMapper(Recipe.ENRICHMENT_CHAMBER));

		if (MachineType.CRUSHER.isEnabled())
			addMapper(new BasicMachineMapper(Recipe.CRUSHER));

		if (MachineType.COMBINER.isEnabled())
			addMapper(new DoubleMachineMapper(Recipe.COMBINER));

		if (MachineType.PURIFICATION_CHAMBER.isEnabled())
			addMapper(new AdvancedMachineMapper(Recipe.PURIFICATION_CHAMBER));

		if (MachineType.OSMIUM_COMPRESSOR.isEnabled())
			addMapper(new AdvancedMachineMapper(Recipe.OSMIUM_COMPRESSOR));

		if (MachineType.CHEMICAL_INJECTION_CHAMBER.isEnabled())
			addMapper(new AdvancedMachineMapper(Recipe.CHEMICAL_INJECTION_CHAMBER));

		if (MachineType.PRECISION_SAWMILL.isEnabled())
			addMapper(new ChanceMachineMapper(Recipe.PRECISION_SAWMILL));

		if (MachineType.METALLURGIC_INFUSER.isEnabled())
			addMapper(new MetallurgicInfuserMapper());

		if (MachineType.CHEMICAL_CRYSTALLIZER.isEnabled())
			addMapper(new CrystallizerMapper());

		if (MachineType.CHEMICAL_DISSOLUTION_CHAMBER.isEnabled())
			addMapper(new DissolutionMapper());

		if (MachineType.CHEMICAL_INFUSER.isEnabled())
			addMapper(new ChemicalInfuserMapper());

		if (MachineType.CHEMICAL_OXIDIZER.isEnabled())
			addMapper(new ChemicalOxidizerMapper());

		if (MachineType.CHEMICAL_WASHER.isEnabled())
			addMapper(new ChemicalWasherMapper());

		if (MachineType.SOLAR_NEUTRON_ACTIVATOR.isEnabled())
			addMapper(new SolarNeutronActivatorMapper());

		if (MachineType.ELECTROLYTIC_SEPARATOR.isEnabled())
			addMapper(new ElectrolyticSeparatorMapper());

		addMapper(new ThermalEvaporationMapper());

		if (MachineType.PRESSURIZED_REACTION_CHAMBER.isEnabled())
			addMapper(new PressurizedReactionChamberMapper());

		if (MachineType.ROTARY_CONDENSENTRATOR.isEnabled())
			addMapper(new RotaryCondensentratorMapper());

		if (MachineType.ENERGIZED_SMELTER.isEnabled())
			addMapper(new BasicMachineMapper(Recipe.ENERGIZED_SMELTER));
		
		GAS_MAP.clear();
		INFUSE_MAP.clear();
	}

	private class BasicMachineMapper extends PEIMapper {
		private final Recipe recipe_type;

		public BasicMachineMapper(Recipe recipe_type) {
			super(recipe_type.toString(), "");
			this.recipe_type = recipe_type;
		}

		@Override
		public void setup() {
			for (Object recipe_raw : recipe_type.get().values()) {
				if (recipe_raw instanceof BasicMachineRecipe) {
					BasicMachineRecipe<?> recipe = (BasicMachineRecipe<?>) recipe_raw;
					addRecipe(recipe.getOutput().output, recipe.getInput().ingredient);
				}
			}
		}
	}

	private class DoubleMachineMapper extends PEIMapper {
		private final Recipe recipe_type;

		public DoubleMachineMapper(Recipe recipe_type) {
			super(recipe_type.toString(), "");
			this.recipe_type = recipe_type;
		}

		@Override
		public void setup() {
			for (Object recipe_raw : recipe_type.get().values()) {
				if (recipe_raw instanceof DoubleMachineRecipe) {
					DoubleMachineRecipe<?> recipe = (DoubleMachineRecipe<?>) recipe_raw;
					addRecipe(recipe.getOutput().output, recipe.getInput().itemStack, recipe.getInput().extraStack);
				}
			}
		}
	}

	private class AdvancedMachineMapper extends PEIMapper {
		private final Recipe recipe_type;

		public AdvancedMachineMapper(Recipe recipe_type) {
			super(recipe_type.toString(), "");
			this.recipe_type = recipe_type;
		}

		@Override
		public void setup() {
			for (Object recipe_raw : recipe_type.get().values()) {
				if (recipe_raw instanceof AdvancedMachineRecipe) {
					AdvancedMachineRecipe<?> recipe = (AdvancedMachineRecipe<?>) recipe_raw;
					if (GAS_MAP.containsKey(recipe.getInput().gasType))
						addRecipe(recipe.getOutput().output, recipe.getInput().itemStack,
								GAS_MAP.get(recipe.getInput().gasType));
					else
						addRecipe(recipe.getOutput().output, recipe.getInput().itemStack);
				}
			}
		}
	}

	private class ChanceMachineMapper extends PEIMapper {
		private final Recipe recipe_type;

		public ChanceMachineMapper(Recipe recipe_type) {
			super(recipe_type.toString(), "");
			this.recipe_type = recipe_type;
		}

		@Override
		public void setup() {
			for (Object recipe_raw : recipe_type.get().values()) {
				if (recipe_raw instanceof ChanceMachineRecipe) {
					ChanceMachineRecipe<?> recipe = (ChanceMachineRecipe<?>) recipe_raw;
					ChanceOutput output = recipe.getOutput();
					if (output.hasPrimary()) {
						addRecipe(output.primaryOutput, recipe.getInput().ingredient);
					}

					if (output.hasSecondary()) {
						if (output.secondaryChance != 100)
							continue;

						addRecipe(output.secondaryOutput, recipe.getInput().ingredient);
					}
				}
			}
		}
	}

	private class MetallurgicInfuserMapper extends PEIMapper {
		public MetallurgicInfuserMapper() {
			super("Metallurgic Infuser", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.METALLURGIC_INFUSER.get().values()) {
				if (recipe_raw instanceof MetallurgicInfuserRecipe) {
					MetallurgicInfuserRecipe recipe = (MetallurgicInfuserRecipe) recipe_raw;
					ItemStack output = recipe.getOutput().output.copy();
					ItemStack input = recipe.getInput().inputStack.copy();
					InfuseStorage infuse = recipe.getInput().infuse;

					if (!INFUSE_MAP.containsKey(infuse.type))
						addConversion(output,
								ImmutableMap.of(input, input.getCount(), INFUSE_MAP.get(infuse.type), infuse.amount));
					else
						addRecipe(output, input);
				}
			}
		}
	}

	private class CrystallizerMapper extends PEIMapper {
		public CrystallizerMapper() {
			super("Crystallizer", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.CHEMICAL_CRYSTALLIZER.get().values()) {
				if (recipe_raw instanceof CrystallizerRecipe) {
					CrystallizerRecipe recipe = (CrystallizerRecipe) recipe_raw;
					GasStack input = recipe.getInput().ingredient;

					if (GAS_MAP.containsKey(input.getGas()))
						addConversion(recipe.getOutput().output.copy(),
								ImmutableMap.of(GAS_MAP.get(input.getGas()), input.amount));
				}
			}
		}
	}

	public abstract class MekanismMapper extends PEIMapper {
		public MekanismMapper(String name, String description) {
			super(name, description);
		}

		protected void addConversion(GasStack gas, Map<Object, Integer> map) {
			if (!GAS_MAP.containsKey(gas.getGas()))
				return;

			addConversion(gas.amount, GAS_MAP.get(gas.getGas()), map);
		}

		protected void addConversion(GasStack output, GasStack... inputs) {
			if (inputs.length == 0)
				return;

			IngredientMap<Object> ingredients = new IngredientMap<Object>();

			for (GasStack input : inputs) {
				if (!GAS_MAP.containsKey(input.getGas()))
					continue;

				ingredients.addIngredient(GAS_MAP.get(input.getGas()), input.amount);
			}

			addConversion(output, ingredients.getMap());
		}
	}

	private class DissolutionMapper extends MekanismMapper {
		public DissolutionMapper() {
			super("Dissolution", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.CHEMICAL_DISSOLUTION_CHAMBER.get().values()) {
				if (recipe_raw instanceof DissolutionRecipe) {
					DissolutionRecipe recipe = (DissolutionRecipe) recipe_raw;
					ItemStack input = recipe.getInput().ingredient;

					addConversion(recipe.getOutput().output, ImmutableMap.of(input, input.getCount()));
				}
			}
		}
	}

	private class ChemicalInfuserMapper extends MekanismMapper {
		public ChemicalInfuserMapper() {
			super("Chemical Infuser", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.CHEMICAL_INFUSER.get().values()) {
				if (recipe_raw instanceof ChemicalInfuserRecipe) {
					ChemicalInfuserRecipe recipe = (ChemicalInfuserRecipe) recipe_raw;
					ChemicalPairInput input = recipe.getInput();
					addConversion(recipe.getOutput().output, input.leftGas, input.rightGas);
				}
			}
		}
	}

	private class ChemicalOxidizerMapper extends MekanismMapper {
		public ChemicalOxidizerMapper() {
			super("Chemical Oxidizer", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.CHEMICAL_OXIDIZER.get().values()) {
				if (recipe_raw instanceof OxidationRecipe) {
					OxidationRecipe recipe = (OxidationRecipe) recipe_raw;
					ItemStack input = recipe.getInput().ingredient;
					addConversion(recipe.getOutput().output, ImmutableMap.of(input, input.getCount()));
				}
			}
		}
	}

	private class ChemicalWasherMapper extends MekanismMapper {
		public ChemicalWasherMapper() {
			super("Chemical Washer", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.CHEMICAL_WASHER.get().values()) {
				if (recipe_raw instanceof WasherRecipe) {
					WasherRecipe recipe = (WasherRecipe) recipe_raw;

					GasStack gas_input = recipe.getInput().ingredient;
					FluidStack fluid_input = recipe.waterInput.ingredient;

					if (GAS_MAP.containsKey(gas_input.getGas()))
						addConversion(recipe.getOutput().output, ImmutableMap.of(GAS_MAP.get(gas_input.getGas()),
								gas_input.amount, fluid_input, fluid_input.amount));
				}
			}
		}
	}

	private class SolarNeutronActivatorMapper extends MekanismMapper {
		public SolarNeutronActivatorMapper() {
			super("Solar Neutron Activator", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.SOLAR_NEUTRON_ACTIVATOR.get().values()) {
				if (recipe_raw instanceof SolarNeutronRecipe) {
					SolarNeutronRecipe recipe = (SolarNeutronRecipe) recipe_raw;
					addConversion(recipe.getOutput().output, recipe.getInput().ingredient);
				}
			}
		}
	}

	private class ElectrolyticSeparatorMapper extends MekanismMapper {
		public ElectrolyticSeparatorMapper() {
			super("Electrolytic Separator", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.ELECTROLYTIC_SEPARATOR.get().values()) {
				if (recipe_raw instanceof SeparatorRecipe) {
					SeparatorRecipe recipe = (SeparatorRecipe) recipe_raw;
					FluidStack input = recipe.recipeInput.ingredient;
					addConversion(recipe.getOutput().leftGas, ImmutableMap.of(input, input.amount));
					addConversion(recipe.getOutput().rightGas, ImmutableMap.of(input, input.amount));
				}
			}
		}
	}
	
	private class ThermalEvaporationMapper extends PEIMapper {
		public ThermalEvaporationMapper() {
			super("Thermal Evaporation", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.THERMAL_EVAPORATION_PLANT.get().values()) {
				if (recipe_raw instanceof ThermalEvaporationRecipe) {
					ThermalEvaporationRecipe recipe = (ThermalEvaporationRecipe) recipe_raw;
					FluidStack input = recipe.recipeInput.ingredient;
					addConversion(recipe.getOutput().output, ImmutableMap.of(input, input.amount));
				}
			}
		}
	}
	
	private class PressurizedReactionChamberMapper extends MekanismMapper {
		public PressurizedReactionChamberMapper() {
			super("Pressurized Reaction Chamber", "");
		}

		@Override
		public void setup() {
			for (Object recipe_raw : Recipe.PRESSURIZED_REACTION_CHAMBER.get().values()) {
				if (recipe_raw instanceof PressurizedRecipe) {
					PressurizedRecipe recipe = (PressurizedRecipe) recipe_raw;
					
					PressurizedInput input = recipe.getInput();
					PressurizedOutput output = recipe.getOutput();
					
					Map<Object, Integer> ingredients = new HashMap<Object, Integer>();
					ingredients.put(input.getFluid(), input.getFluid().amount);
					ingredients.put(input.getSolid(), input.getSolid().getCount());
					
					if (GAS_MAP.containsKey(input.getGas().getGas()))
						ingredients.put(GAS_MAP.get(input.getGas().getGas()), input.getGas().amount);
					
					if (GAS_MAP.containsKey(output.getGasOutput().getGas()))
						addConversion(output.getGasOutput(), ingredients);
					
					addConversion(output.getItemOutput(), ingredients);
				}
			}
		}
	}
	
	private class RotaryCondensentratorMapper extends MekanismMapper {
		public RotaryCondensentratorMapper() {
			super("Rotary Condensentrator", "");
		}

		@Override
		public void setup() {
			for (Gas gas : GasRegistry.getRegisteredGasses()) {
				if (gas.hasFluid() && GAS_MAP.containsKey(gas)) {
					addConversion(new GasStack(gas, 1), ImmutableMap.of(gas.getFluid(), 1));
					addConversion(new FluidStack(gas.getFluid(), 1), ImmutableMap.of(GAS_MAP.get(gas), 1));
				}
			}
		}
	}
}
