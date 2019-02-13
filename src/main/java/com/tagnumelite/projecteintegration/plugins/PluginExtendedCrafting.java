package com.tagnumelite.projecteintegration.plugins;

import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.other.Utils;

import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@Plugin(modid="extendedcrafting")
public class PluginExtendedCrafting implements IPlugin {
	private static boolean enable_compressor_conversions;
	private static boolean enable_combination_conversions;
	private static boolean enable_ender_conversions;
	private static boolean enable_table_conversions;
	
	@Override
	public void addConfig(Configuration config, String category) {
		enable_compressor_conversions = config.getBoolean("enable_compressor_conversions", category, true, "Enable Compressor Recipe Conversions?");
		enable_combination_conversions = config.getBoolean("enable_combination_conversions", category, true, "Enable Combination Recipe Conversions?");
		enable_ender_conversions = config.getBoolean("enable_ender_conversions", category, true, "Enable Ender Crafting Table Recipe Conversions?");
		enable_table_conversions = config.getBoolean("enable_table_conversions", category, true, "Enable Tiered Crafting Table Recipe Conversions?");
	}
	
	@Override
	public void addEMC(IEMCProxy proxy) {}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addConversions(IConversionProxy proxy) {
		if (enable_compressor_conversions) {
			for (CompressorRecipe recipe : CompressorRecipeManager.getInstance().getRecipes()) {
				ItemStack output = recipe.getOutput();
				if (output.isEmpty())
					continue;
				
				Object input = recipe.getInput();
				
				if (input instanceof NonNullList<?>)
					input = Utils.createFromList(proxy, input);
					
				proxy.addConversion(output.getCount(), output, ImmutableMap.of(input, recipe.getInputCount()));
				//TODO: DEBUG RECIPE LOG
			}
		}
		
		if (enable_combination_conversions) {
			for (CombinationRecipe recipe : CombinationRecipeManager.getInstance().getRecipes()) {
				ItemStack output = recipe.getOutput();
				ItemStack input = recipe.getInput();
				if (output.isEmpty() || input.isEmpty())
					continue;
				
				IngredientMap ingredients = new IngredientMap();
				
				ingredients.addIngredient(input, input.getCount());
				
				for (Object pedestal : recipe.getPedestalItems()) {
					if (pedestal instanceof NonNullList<?>)
						ingredients.addIngredient(Utils.createFromList(proxy, pedestal), 1);
					else
						ingredients.addIngredient(pedestal, 1);
				}
				
				proxy.addConversion(output.getCount(), output, ingredients.getMap());
				//TODO: DEBUG RECIPE LOG
			}
		}
		
		if (enable_ender_conversions) {
			for (Object obj : EnderCrafterRecipeManager.getInstance().getRecipes()) {
				IRecipe recipe = (IRecipe) obj;
				if (recipe.getRecipeOutput().isEmpty())
					continue;
				
				Utils.addConversion(proxy, recipe);
				//TODO: DEBUG RECIPE LOG
			}
		}
		
		if (enable_table_conversions) {
			for (Object obj : TableRecipeManager.getInstance().getRecipes()) {
				IRecipe recipe = (IRecipe) obj;
				if (recipe.getRecipeOutput().isEmpty())
					continue;
				
				Utils.addConversion(proxy, recipe);
				//TODO: DEBUG RECIPE LOG
			}
		}
	}
}
