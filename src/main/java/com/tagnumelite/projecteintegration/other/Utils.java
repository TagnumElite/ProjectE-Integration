package com.tagnumelite.projecteintegration.other;

import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class Utils {
	public static Object createFromIngredient(IConversionProxy proxy, Ingredient ingredient) {
		Object obj = new Object();
		
		for (ItemStack stack : ingredient.getMatchingStacks())
			proxy.addConversion(1, obj, ImmutableMap.of((Object)stack, 1));
		
		return obj;
	}
	
	public static Object createFromList(IConversionProxy proxy, Object input) {
		Object obj = new Object();
		
		for (Object object : (NonNullList<?>) input)
			proxy.addConversion(1, obj, ImmutableMap.of(object, 1));
		
		return obj;
	}
	
	public static void addConversion(IConversionProxy proxy, IRecipe recipe) {
		ItemStack output = recipe.getRecipeOutput();
		IngredientMap<Object> ingredients = new IngredientMap<Object>();

		for (Ingredient ingredient : recipe.getIngredients()) {
			if (ingredient == Ingredient.EMPTY)
				continue;

			ItemStack[] matching = ingredient.getMatchingStacks();
			if (matching .length <= 0)
				continue;

			Object obj = new Object();
			for (ItemStack item : matching)
				proxy.addConversion(1, obj, ImmutableMap.of(item, 1));

			ingredients.addIngredient(obj, 1);
		}

		proxy.addConversion(output.getCount(), output, ingredients.getMap());
	}
	
	public static void debugRecipe(String machine, ItemStack output, Ingredient... inputs) {
		debugRecipe(machine, output, inputs[0].getMatchingStacks()[0]);
	}
	
	public static void debugRecipe(String machine, ItemStack output, ItemStack... inputs) {
		
	}

	@SuppressWarnings("rawtypes")
	public static void debugRecipe(String machine, ItemStack output, IngredientMap ingredients) {
		
	}
}
