package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.List;

import moze_intel.projecte.emc.IngredientMap;
import nc.recipe.NCRecipes;
import nc.recipe.NCRecipes.Type;
import nc.recipe.ProcessorRecipe;
import nc.recipe.ProcessorRecipeHandler;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@RegPEIPlugin(modid = "nuclearcraft")
public class PluginNuclearCraft extends PEIPlugin {
	public PluginNuclearCraft(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (Type recipe_type : NCRecipes.Type.values()) {
			addMapper(new NCRecipeMapper(recipe_type));
		}
	}

	private class NCRecipeMapper extends PEIMapper {
		private Type recipe_type;

		public NCRecipeMapper(Type recipe_type) {
			super(recipe_type.toString(), "Enable Recipe mapper for this machine?");
			this.recipe_type = recipe_type;
		}

		private Object getObjectFromItemIngredient(IItemIngredient item) {
			Object obj = new Object();
			for (ItemStack input : item.getInputStackList()) {
				addConversion(1, obj, ImmutableMap.of((Object) input, input.getCount()));
			}
			return obj;
		}

		private Object getObjectFromFluidIngredient(IFluidIngredient fluid) {
			Object obj = new Object();
			for (FluidStack input : fluid.getInputStackList()) {
				addConversion(1, obj, ImmutableMap.of((Object) input, input.amount));
			}
			return obj;
		}

		@Override
		public void setup() {
			final ProcessorRecipeHandler recipe_handler = recipe_type.getRecipeHandler();

			for (ProcessorRecipe recipe : recipe_handler.recipes) {
				final int fluid_input_size = recipe_handler.fluidInputSize;
				final int fluid_output_size = recipe_handler.fluidOutputSize;
				final int item_input_size = recipe_handler.itemInputSize;
				final int item_output_size = recipe_handler.itemOutputSize;

				List<IItemIngredient> item_inputs = recipe.itemIngredients();
				List<IFluidIngredient> fluid_inputs = recipe.fluidIngredients();

				List<IItemIngredient> item_outputs = recipe.itemProducts();
				List<IFluidIngredient> fluid_outputs = recipe.fluidProducts();

				if ((item_output_size == 0 && fluid_output_size == 0) || (fluid_input_size == 0 && item_input_size == 0)
						|| (item_outputs.size() != item_output_size) || (fluid_outputs.size() != fluid_output_size)
						|| (item_inputs.size() != item_input_size) || (fluid_inputs.size() != fluid_input_size)) {
					PEIntegration.LOG.warn("Invalid Recipe: {}", recipe_handler.getRecipeName());
					continue;
				}

				IngredientMap<Object> ingredients = new IngredientMap<Object>();

				for (IItemIngredient input : item_inputs) {
					ingredients.addIngredient(getObjectFromItemIngredient(input), input.getMaxStackSize());
				}

				for (IFluidIngredient input : fluid_inputs) {
					ingredients.addIngredient(getObjectFromFluidIngredient(input), input.getMaxStackSize());
				}

				for (IItemIngredient output : item_outputs) {
					for (ItemStack output_item : output.getInputStackList()) {
						if (output_item == null || output_item.isEmpty())
							continue;

						addConversion(output_item, ingredients.getMap());
					}
				}

				for (IFluidIngredient output : fluid_outputs) {
					for (FluidStack output_fluid : output.getInputStackList()) {
						if (output_fluid == null || output_fluid.amount == 0)
							continue;

						addConversion(output_fluid, ingredients.getMap());
					}
				}
			}
		}
	}
}
