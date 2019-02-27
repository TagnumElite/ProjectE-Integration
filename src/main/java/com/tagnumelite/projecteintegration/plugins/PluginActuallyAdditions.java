package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.IPlugin;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.other.Utils;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin(modid="actuallyadditions")
public class PluginActuallyAdditions implements IPlugin {
	private static boolean enable_empowerer;
	private static boolean enable_reconstructor;
	private static boolean enable_crusher;
	
	@Override
	public void addConfig(Configuration config, String category) {
		enable_empowerer = config.getBoolean("enable_empowerer_conversion", category, true, "Enable Automated EMC conversions for the Empowerer");
		enable_reconstructor = config.getBoolean("enable_reconstructor_conversion", category, true, "Enable Automated EMC conversions for the Atomic Reconstructor");
		enable_crusher = config.getBoolean("enable_crusher_conversion", category, true, "Enable Automated EMC conversions for the Crusher");
	}

	@Override
	public void addEMC(IEMCProxy proxy) {}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addConversions(IConversionProxy proxy) {
		// Empowerer Recipes
		if (enable_empowerer) {
			for (EmpowererRecipe recipe : ActuallyAdditionsAPI.EMPOWERER_RECIPES) {
				ItemStack output = recipe.getOutput();
				if (output.isEmpty())
					continue;
				Ingredient input = recipe.getInput();
				if (input == Ingredient.EMPTY)
					continue;
				
				IngredientMap ingredients = new IngredientMap();
				ingredients.addIngredient(Utils.createFromIngredient(proxy, input), 1);
				
				Ingredient stand1 = recipe.getStandOne();
				if (stand1 != Ingredient.EMPTY)
					ingredients.addIngredient(Utils.createFromIngredient(proxy, stand1), 1);
				Ingredient stand2 = recipe.getStandTwo();
				if (stand2 != Ingredient.EMPTY)
					ingredients.addIngredient(Utils.createFromIngredient(proxy, stand2), 1);
				Ingredient stand3 = recipe.getStandThree();
				if (stand3 != Ingredient.EMPTY)
					ingredients.addIngredient(Utils.createFromIngredient(proxy, stand3), 1);
				Ingredient stand4 = recipe.getStandFour();
				if (stand4 != Ingredient.EMPTY)
					ingredients.addIngredient(Utils.createFromIngredient(proxy, stand4), 1);
				
				proxy.addConversion(output.getCount(), output, ingredients.getMap());
				Utils.debugRecipe("Empowerer", output, ingredients);
			}
		}
		
		//Reconstructor Recipes
		if (enable_reconstructor) {
			for (LensConversionRecipe recipe : ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES) {
				ItemStack output = recipe.getOutput();
				if (output.isEmpty())
					continue;
				
				Ingredient input = recipe.getInput();
				if (input == Ingredient.EMPTY)
					continue;
				
				proxy.addConversion(output.getCount(), output, ImmutableMap.of(Utils.createFromIngredient(proxy, input), 1));
				Utils.debugRecipe("Atomic Reconstructor", output, input);
			}
		}
		
		//Crusher Recipes
		if (enable_crusher) {
			for (CrusherRecipe recipe : ActuallyAdditionsAPI.CRUSHER_RECIPES) {
				ItemStack output = recipe.getOutputOne();
				if (output.isEmpty())
					continue;
				
				Ingredient input = recipe.getInput();
				if (input == Ingredient.EMPTY)
					continue;
				
				proxy.addConversion(output.getCount(), output, ImmutableMap.of(Utils.createFromIngredient(proxy, input), 1));
				Utils.debugRecipe("Crusher", output, input);
			}
		}
	}

	@Override
	public void addBlacklist(IBlacklistProxy proxy) {}

	@Override
	public void addTransmutation(ITransmutationProxy proxy) {}
}
