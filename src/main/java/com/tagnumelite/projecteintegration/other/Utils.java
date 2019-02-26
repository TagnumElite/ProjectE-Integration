package com.tagnumelite.projecteintegration.other;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

public class Utils {
	private static HashMap<Ingredient, Object> MAP_INGREDIENTS = new HashMap<Ingredient, Object>();
	
	public static Object createFromIngredient(IConversionProxy proxy, Ingredient ingredient) {
		if (ingredient == Ingredient.EMPTY)
			return null;
		
		if (MAP_INGREDIENTS.containsKey(ingredient))
			return MAP_INGREDIENTS.get(ingredient);
		
		Object obj = new Object();
		
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			if (stack == null || stack.isEmpty())
				continue;
			
			proxy.addConversion(1, obj, ImmutableMap.of(stack, 1));
		}
		
		MAP_INGREDIENTS.put(ingredient, obj);
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

			ingredients.addIngredient(createFromIngredient(proxy, ingredient), 1);
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

	public static void clearCache() {
		MAP_INGREDIENTS.clear();
	}
}
