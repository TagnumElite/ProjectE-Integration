package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.other.Utils;

import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@Plugin(modid="avaritia")
public class PluginAvaritia implements IPlugin {
	private static boolean enable_extreme_conversions;
	private static boolean enable_compressor_conversions;

	@Override
	public void addConfig(Configuration config, String category) {
		enable_extreme_conversions = config.getBoolean("enable_extreme_conversions", category, true, "Enable EMC conversions for the Extreme Crafting Table");
		enable_compressor_conversions = config.getBoolean("enable_extreme_conversions", category, true, "Enable EMC conversions for the Compressor");
	}

	@Override
	public void addEMC(IEMCProxy proxy) {}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addConversions(IConversionProxy proxy) {
		if (enable_extreme_conversions) {
			for (IExtremeRecipe recipe : AvaritiaRecipeManager.EXTREME_RECIPES.values()) {
				ItemStack output = recipe.getRecipeOutput();
				if (output.isEmpty())
					continue;
				
				NonNullList<Ingredient> inputs = recipe.getIngredients();
				
				IngredientMap ingredients = new IngredientMap();
	
				for (Ingredient input : inputs) {
					if (input == Ingredient.EMPTY)
						continue;
					
					ingredients.addIngredient(Utils.createFromIngredient(proxy, input), 1);
				}
				
				proxy.addConversion(output.getCount(), output, ingredients.getMap());
			}
		}
		
		if (enable_compressor_conversions) {
			for (ICompressorRecipe recipe : AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()) {
				ItemStack output = recipe.getResult();
				if (output.isEmpty())
					continue;
				
				proxy.addConversion(output.getCount(), output, ImmutableMap.of(Utils.createFromIngredient(proxy, recipe.getIngredients().get(0)), recipe.getCost()));
			}
		}
	}

	@Override
	public void addBlacklist(IBlacklistProxy proxy) {}

	@Override
	public void addTransmutation(ITransmutationProxy proxy) {}
}
