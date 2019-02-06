package com.tagnumelite.projecteintegration.plugins;

import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.utils.Utils;

import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

public class PluginExtendedCrafting {
	public static void addEMC(IEMCProxy proxy) {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addConversions(IConversionProxy proxy) {
		for (CompressorRecipe recipe : CompressorRecipeManager.getInstance().getRecipes()) {
			ItemStack output = recipe.getOutput();
			if (output.isEmpty())
				continue;
			
			Object input = recipe.getInput();
			
			if (input instanceof NonNullList<?>)
				input = Utils.createFromList(proxy, input);
				
			proxy.addConversion(output.getCount(), output, ImmutableMap.of(input, recipe.getInputCount()));
		}
		
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
		}
		
		for (Object obj : EnderCrafterRecipeManager.getInstance().getRecipes()) {
			IRecipe recipe = (IRecipe) obj;
			if (recipe.getRecipeOutput().isEmpty())
				continue;
			
			Utils.addConversion(proxy, recipe);
		}
		
		for (Object obj : TableRecipeManager.getInstance().getRecipes()) {
			IRecipe recipe = (IRecipe) obj;
			if (recipe.getRecipeOutput().isEmpty())
				continue;
			
			Utils.addConversion(proxy, recipe);
		}
	}
}
