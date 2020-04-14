package com.tagnumelite.projecteintegration.plugins.tech;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import moze_intel.projecte.emc.IngredientMap;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin("immersiveengineering")
public class PluginImmersiveEngineering extends APEIPlugin {
	public PluginImmersiveEngineering(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new BlastFurnaceMapper());
		addMapper(new CokeOvenMapper());
		addMapper(new CrusherMapper());
		addMapper(new EnginnerWorkbenchMapper());
		addMapper(new KilnMapper());
		addMapper(new MetalPressMapper());
	}

	private static class BlastFurnaceMapper extends PEIMapper {
		public BlastFurnaceMapper() {
			super("Blast Furnace");
		}

		@Override
		public void setup() {
			for (BlastFurnaceRecipe recipe : BlastFurnaceRecipe.recipeList) {
				ItemStack output = recipe.output;
				if (output == null || output.isEmpty())
					continue;

				Object input = recipe.input;
				if (input == null)
					continue;

				if (input instanceof List)
					input = PEIApi.getList((List<?>) input);
				else if (!(input instanceof ItemStack) && !(input instanceof String))
					continue;

				addConversion(output, ImmutableMap.of(input, 1));

				ItemStack slag = recipe.slag;
				if (slag == null || slag.isEmpty())
					continue;

				addConversion(slag, ImmutableMap.of(input, 1));
			}
		}
	}

	private static class CokeOvenMapper extends PEIMapper {
		public CokeOvenMapper() {
			super("Coke Oven");
		}

		@Override
		public void setup() {
			for (CokeOvenRecipe recipe : CokeOvenRecipe.recipeList) {
				ItemStack output = recipe.output;
				if (output == null || output.isEmpty())
					continue;

				Object input = recipe.input;
				if (input == null) {
					continue;
				} else if (input instanceof ItemStack) {
					if (((ItemStack) input).isEmpty())
						continue;
				} else if (input instanceof String || input instanceof Item || input instanceof Block) {
					//DO NOTHING!
				} else if (input instanceof Ingredient) {
					input = PEIApi.getIngredient((Ingredient) input);
				} else if (input instanceof List) {
					input = PEIApi.getList((List<?>) input);
				} else {
					PEIApi.LOG.debug("Coke Oven Mapper: Unknown Input: {}, ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
					continue;
				}

				if (recipe.creosoteOutput > 0)
					addRecipe(recipe.creosoteOutput, IEContent.fluidCreosote, ImmutableMap.of(input, 1));

				PEIApi.LOG.debug("Coke Oven Input: {}", ClassUtils.getPackageCanonicalName(input.getClass()));

				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}

	private static class KilnMapper extends PEIMapper {
		public KilnMapper() {
			super("Kiln");
		}

		@Override
		public void setup() {
			for (AlloyRecipe recipe : AlloyRecipe.recipeList) {
				ItemStack output = recipe.output;
				if (output == null || output.isEmpty())
					continue;

				IngredientStack input1 = recipe.input0;
				if (input1 == null || input1.inputSize <= 0)
					continue;

				IngredientStack input2 = recipe.input1;
				if (input2 == null || input2.inputSize <= 0)
					continue;
				
				//addRecipe(output.copy(), input1.) TODO: Output for kiln mapper
			}
		}
	}

	private abstract static class MultiblockRecipeMapper extends PEIMapper {
		public MultiblockRecipeMapper(String name) {
			super(name);
		}

		protected void addRecipe(MultiblockRecipe recipe) {
			List<Ingredient> item_inputs = new ArrayList<Ingredient>();
			recipe.getItemInputs().forEach(r -> {
				item_inputs.add(r.toRecipeIngredient());
			});
			List<FluidStack> fluid_inputs = recipe.getFluidInputs();

			NonNullList<ItemStack> item_outputs = recipe.getItemOutputs();
			List<FluidStack> fluid_outputs = recipe.getFluidOutputs();

			IngredientMap<Object> ingredients = new IngredientMap<Object>();

			if (item_inputs != null && !item_inputs.isEmpty()) {
				for (Ingredient input : item_inputs) {
					if (input == null || input == Ingredient.EMPTY)
						continue;
					
					ingredients.addIngredient(PEIApi.getIngredient(input), 1);
				}
			}

			if (fluid_inputs != null && !fluid_inputs.isEmpty()) {
				for (FluidStack input : fluid_inputs) {
					if (input.amount <= 0)
						continue;

					ingredients.addIngredient(input, input.amount);
				}
			}

			Map<Object, Integer> input_map = ingredients.getMap();
			if (input_map == null || input_map.isEmpty())
				return;
			
			ArrayList<Object> outputs = new ArrayList<>();

			if (item_outputs != null && !item_outputs.isEmpty()) {
				for (ItemStack output : item_outputs) {
					if (output == null || output.isEmpty())
						continue;

					//addConversion(output, input_map);
					outputs.add(output);
				}
			}

			if (fluid_outputs != null && !fluid_outputs.isEmpty()) {
				for (FluidStack output : fluid_outputs) {
					if (output == null || output.amount <= 0)
						continue;

					//addConversion(output, input_map);
					outputs.add(output);
				}
			}
			
			addConversion(outputs, input_map);
		}
	}

	private static class CrusherMapper extends MultiblockRecipeMapper {
		public CrusherMapper() {
			super("Crusher");
		}

		@Override
		public void setup() {
			for (CrusherRecipe recipe : CrusherRecipe.recipeList) {
				addRecipe(recipe);
			}
		}
	}

	private static class EnginnerWorkbenchMapper extends MultiblockRecipeMapper {
		public EnginnerWorkbenchMapper() {
			super("Enginner Workbench");
		}

		@Override
		public void setup() {
			for (BlueprintCraftingRecipe recipe : BlueprintCraftingRecipe.recipeList.values()) {
				addRecipe(recipe);
			}
		}
	}

	private static class MetalPressMapper extends MultiblockRecipeMapper {
		public MetalPressMapper() {
			super("Metal Press");
		}

		@Override
		public void setup() {
			for (MetalPressRecipe recipe : MetalPressRecipe.recipeList.values()) {
				addRecipe(recipe);
			}
		}
	}
}
