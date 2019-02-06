package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.ProjectEIntegration;
import com.tagnumelite.projecteintegration.utils.Utils;

import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import de.ellpeck.actuallyadditions.mod.blocks.InitBlocks;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class PluginActuallyAdditions {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addConversions(IConversionProxy proxy) {
		// Empowerer Recipes
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
			ProjectEIntegration.LOG.info("Item: {} registered: Count: {}", output.getDisplayName(), output.getCount());
		}
		
		//Reconstructor Recipes
		for (LensConversionRecipe recipe : ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES) {
			ItemStack output = recipe.getOutput();
			if (output.isEmpty())
				continue;
			Ingredient input = recipe.getInput();
			if (input == Ingredient.EMPTY)
				continue;
			
			proxy.addConversion(output.getCount(), output, ImmutableMap.of(Utils.createFromIngredient(proxy, input), 1));
			ProjectEIntegration.LOG.info("Item: {} registered: Count: {}", output.getDisplayName(), output.getCount());
		}
		
		//Crusher Recipes
		for (CrusherRecipe recipe : ActuallyAdditionsAPI.CRUSHER_RECIPES) {
			Ingredient input = recipe.getInput();
			if (input == Ingredient.EMPTY)
				continue;
			
			ItemStack output = recipe.getOutputOne();
			if (output.isEmpty())
				continue;
			
			proxy.addConversion(output.getCount(), output, ImmutableMap.of(Utils.createFromIngredient(proxy, input), 1));
		}
	}
}
