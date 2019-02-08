package com.tagnumelite.projecteintegration.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.ProjectEIntegration;

import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

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
	
	/**
	 * Shamelessly stolen from {@link https://github.com/mezz/JustEnoughItems/blob/1.12/src/main/java/mezz/jei/startup/AnnotatedInstanceUtil.java#L22-L37}
	 * 
	 * @author mezz
	 * 
	 * @param asmDataTable
	 * @param classAnnotation
	 * @param classInterface
	 * @return
	 */
	public static <T> List<T> getPlugins(ASMDataTable asmDataTable, @SuppressWarnings("rawtypes") Class classAnnotation, Class<T> classInterface) {
		String annotationClassName = classAnnotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<T> instances = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			try {
				Class<?> asmClass = Class.forName(asmData.getClassName());
				Class<? extends T> asmInstanceClass = asmClass.asSubclass(classInterface);
				T instance = asmInstanceClass.newInstance();
				instances.add(instance);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
				ProjectEIntegration.LOG.error("Failed to load: {}", asmData.getClassName(), e);
			}
		}
		return instances;
	}
}
