package com.tagnumelite.projecteintegration.plugins;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

@RegPEIPlugin(modid = "immersiveengineering")
public class PluginImmersiveEngineering extends PEIPlugin {
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

	private class BlastFurnaceMapper extends PEIMapper {
		public BlastFurnaceMapper() {
			super("Blast Furnace", "");
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

	private class CokeOvenMapper extends PEIMapper {
		public CokeOvenMapper() {
			super("Coke Oven", "");
		}

		@Override
		public void setup() {
			for (CokeOvenRecipe recipe : CokeOvenRecipe.recipeList) {
				ItemStack output = recipe.output;
				if (output == null || output.isEmpty())
					continue;

				Object input = recipe.input;
				if (input == null)
					continue;
				else if (input instanceof ItemStack)
					if (((ItemStack) input).isEmpty())
						continue;
					else if (input instanceof String)
						input = PEIApi.getList(OreDictionary.getOres((String) input));
					else if (input instanceof Ingredient)
						input = PEIApi.getIngredient((Ingredient) input);
					else
						continue; // TODO: Log Unknown Input

				if (recipe.creosoteOutput > 0)
					addConversion(recipe.creosoteOutput, IEContent.fluidCreosote, ImmutableMap.of());

				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}

	private class KilnMapper extends PEIMapper {
		public KilnMapper() {
			super("Kiln", "");
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
			}
		}
	}

	private abstract class MultiblockRecipeMapper extends PEIMapper {
		public MultiblockRecipeMapper(String name, String description) {
			super(name, description);
		}

		protected void addRecipe(MultiblockRecipe recipe) {
			List<Ingredient> item_inputs = new ArrayList<Ingredient>();
			recipe.getItemInputs().forEach(r -> {
				item_inputs.add(r.toRecipeIngredient());
			});
			List<FluidStack> fluid_inputs = recipe.getFluidInputs();

			NonNullList<ItemStack> item_outputs = recipe.getItemOutputs();
			List<FluidStack> fluid_outputs = recipe.getFluidOutputs();

			if (item_outputs != null && !item_outputs.isEmpty()) {
				for (ItemStack output : item_outputs) {
					addRecipe(output, fluid_inputs.toArray(), item_inputs.toArray());
				}
			}

			if (fluid_outputs != null && !fluid_outputs.isEmpty()) {
				for (FluidStack output : fluid_outputs) {
					addRecipe(output, fluid_inputs.toArray(), item_inputs.toArray());
				}
			}
		}
	}

	private class CrusherMapper extends MultiblockRecipeMapper {
		public CrusherMapper() {
			super("Crusher", "");
		}

		@Override
		public void setup() {
			for (CrusherRecipe recipe : CrusherRecipe.recipeList) {
				addRecipe(recipe);
			}
		}
	}

	private class EnginnerWorkbenchMapper extends MultiblockRecipeMapper {
		public EnginnerWorkbenchMapper() {
			super("Enginner Workbench", "");
		}

		@Override
		public void setup() {
			for (BlueprintCraftingRecipe recipe : BlueprintCraftingRecipe.recipeList.values()) {
				addRecipe(recipe);
			}
		}
	}

	private class MetalPressMapper extends MultiblockRecipeMapper {
		public MetalPressMapper() {
			super("Metal Press", "");
		}

		@Override
		public void setup() {
			for (MetalPressRecipe recipe : MetalPressRecipe.recipeList.values()) {
				addRecipe(recipe);
			}
		}
	}
}
