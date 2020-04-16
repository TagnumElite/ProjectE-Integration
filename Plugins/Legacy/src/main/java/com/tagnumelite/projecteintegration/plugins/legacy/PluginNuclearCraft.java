package com.tagnumelite.projecteintegration.plugins.legacy;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import moze_intel.projecte.emc.IngredientMap;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.IRecipe;
import nc.recipe.NCRecipes;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin("nuclearcraft")
@OnlyIf(versionStartsWith="2.")
public class PluginNuclearCraft extends APEIPlugin {
	public PluginNuclearCraft(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		ArrayList<AbstractRecipeHandler<? extends IRecipe>> handlers = new ArrayList<>();

		Collections.addAll(handlers, NCRecipes.alloy_furnace, NCRecipes.centrifuge, NCRecipes.chemical_reactor,
				NCRecipes.collector, NCRecipes.condenser, NCRecipes.coolant_heater, NCRecipes.crystallizer,
				NCRecipes.decay_generator, NCRecipes.decay_hastener, NCRecipes.dissolver, NCRecipes.electrolyser,
				NCRecipes.extractor, NCRecipes.fission, NCRecipes.fuel_reprocessor, NCRecipes.fusion,
				NCRecipes.heat_exchanger, NCRecipes.infuser, NCRecipes.ingot_former, NCRecipes.irradiator,
				NCRecipes.isotope_separator, NCRecipes.manufactory, NCRecipes.melter, NCRecipes.pressurizer,
				NCRecipes.rock_crusher, NCRecipes.salt_fission, NCRecipes.salt_mixer, NCRecipes.supercooler,
				NCRecipes.turbine);

		for (AbstractRecipeHandler<? extends IRecipe> handler : handlers) {
			addMapper(new AbstractRecipeMapper(handler));
		}
	}

	private static class AbstractRecipeMapper extends PEIMapper {
		private AbstractRecipeHandler<? extends IRecipe> handler;

		public AbstractRecipeMapper(AbstractRecipeHandler<? extends IRecipe> handler) {
			super(handler.getRecipeName());
			this.handler = handler;
		}

		private Object getObjectFromItemIngredient(IItemIngredient item) {
			Object obj = new Object();
			for (ItemStack input : item.getInputStackList()) {
				addConversion(1, obj, ImmutableMap.of(input, input.getCount()));
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
			for (IRecipe recipe : handler.getRecipeList()) {
				List<IItemIngredient> item_inputs = recipe.itemIngredients();
				List<IFluidIngredient> fluid_inputs = recipe.fluidIngredients();

				List<IItemIngredient> item_outputs = recipe.itemProducts();
				List<IFluidIngredient> fluid_outputs = recipe.fluidProducts();

				if ((item_inputs.size() <= 0 && fluid_inputs.size() <= 0)
						|| (item_outputs.size() <= 0 && fluid_outputs.size() <= 0)) {
					PEIntegration.LOG.warn("Invalid Recipe from `{}`", handler.getRecipeName());
					continue;
				}

				IngredientMap<Object> ingredients = new IngredientMap<>();

				for (IItemIngredient input : item_inputs) {
					ingredients.addIngredient(getObjectFromItemIngredient(input), input.getMaxStackSize(0));
				}

				for (IFluidIngredient input : fluid_inputs) {
					ingredients.addIngredient(getObjectFromFluidIngredient(input), input.getMaxStackSize(0));
				}

				ArrayList<Object> output = new ArrayList<>();
				item_outputs.forEach(item -> {
					PEIApi.LOG.debug("Item Input: {}; Output {};", item.getInputStackList(), item.getOutputStackList());
					output.addAll(item.getOutputStackList());
				});
				fluid_outputs.forEach(fluid -> {
					PEIApi.LOG.debug("Fluid Input: {}; Output {};", fluid.getInputStackList(),
							fluid.getOutputStackList());
					output.addAll(fluid.getInputStackList());
				});

				addConversion(output, ingredients.getMap());
			}
		}
	}
}
