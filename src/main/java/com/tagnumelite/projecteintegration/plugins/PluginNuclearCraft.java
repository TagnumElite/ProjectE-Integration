package com.tagnumelite.projecteintegration.plugins;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.PEIntegration;

import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
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

@Plugin(modid="nuclearcraft")
public class PluginNuclearCraft implements IPlugin {
	private Configuration config;
	private String category;

	@Override
	public void addConfig(Configuration config, String category) {
		this.config = config;
		this.category = category;
	}

	@Override
	public void addEMC(IEMCProxy proxy) {}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addConversions(IConversionProxy proxy) {
		
		for (Type recipe_type : NCRecipes.Type.values()) {
			if (config.getBoolean("enable_" + recipe_type.toString().toLowerCase() + "_conversions", category, true, "")) {
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
					
					if ((item_output_size == 0 && fluid_output_size == 0) || (fluid_input_size == 0 && item_input_size == 0) ||
						(item_outputs.size() != item_output_size) || (fluid_outputs.size() != fluid_output_size) ||
						(item_inputs.size() != item_input_size) || (fluid_inputs.size() != fluid_input_size)) {
						continue;
					}
					
					IngredientMap ingredients = new IngredientMap();
					
					for (IItemIngredient input : item_inputs) {
						ingredients.addIngredient(getObjectFromItemIngredient(proxy, input), input.getMaxStackSize());
					}
					
					for (IFluidIngredient input : fluid_inputs) {
						ingredients.addIngredient(getObjectFromFluidIngredient(proxy, input), input.getMaxStackSize());
					}
					
					for (IItemIngredient output : item_outputs) {
						for (ItemStack output_item : output.getInputStackList()) {
							if (output_item == null || output_item.isEmpty())
								continue;
							
							proxy.addConversion(output_item.getCount(), output_item, ingredients.getMap());
						}
					}
					
					for (IFluidIngredient output : fluid_outputs) {
						for (FluidStack output_fluid : output.getInputStackList()) {
							if (output_fluid == null || output_fluid.amount == 0)
								continue;
							
							proxy.addConversion(output_fluid.amount, output_fluid, ingredients.getMap());
						}
					}
				}
			}
		}
	}

	@Override
	public void addBlacklist(IBlacklistProxy proxy) {}

	@Override
	public void addTransmutation(ITransmutationProxy proxy) {}
	
	private Object getObjectFromItemIngredient(IConversionProxy proxy, IItemIngredient item) {
		Object obj = new Object();
		
		for (ItemStack input : item.getInputStackList()) {
			proxy.addConversion(1, obj, ImmutableMap.of(input, input.getCount()));
		}
		
		return obj;
	}
	
	private Object getObjectFromFluidIngredient(IConversionProxy proxy, IFluidIngredient fluid) {
		Object obj = new Object();
		
		for (FluidStack input : fluid.getInputStackList()) {
			proxy.addConversion(1, obj, ImmutableMap.of(input, input.amount));
		}
		
		return obj;
	}
}
