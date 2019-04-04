package com.tagnumelite.projecteintegration.api.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import com.tagnumelite.projecteintegration.api.PEIApi;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class Utils {
	/**
	 * Compare {@code ItemStack}s against each other
	 * 
	 * @param stack1
	 *            {@code ItemStack} The stack to compare against
	 * @param stack2
	 *            {@code ItemStack} The stack to compare with
	 * @return {@code boolean} Wether or not they are the same item
	 */
	public static boolean isSameItem(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == stack1.getItem()
				&& (stack2.getItemDamage() == 32767 || stack2.getItemDamage() == stack1.getItemDamage());
	}

	public static IngredientMap<Object> createInputs(Object... inputs) {
		return getInputsFirst(new IngredientMap<Object>(), inputs);
	}

	public static IngredientMap<Object> getInputsFirst(IngredientMap<Object> ingredients, Object... inputs) {
		for (Object input : inputs) {
			if (input == null)
				continue;

			if (input instanceof ItemStack) {
				if (((ItemStack) input).isEmpty())
					continue;

				ingredients.addIngredient(input, ((ItemStack) input).getCount());
			} else if (input instanceof Item || input instanceof Block || input instanceof String
					|| input.getClass().equals(Object.class)) {
				ingredients.addIngredient(input, 1);
			} else if (input instanceof FluidStack) {
				if (((FluidStack) input).amount <= 0)
					continue;

				ingredients.addIngredient(input, ((FluidStack) input).amount);
			} else if (input instanceof List) {
				List<?> inputt = (List<?>) input;
				if (inputt.isEmpty())
					continue;

				if (inputt.size() == 1) {
					getInputsFirst(ingredients, inputt.get(0));
					continue;
				}

				ingredients.addIngredient(PEIApi.getList((List<?>) input), 1);
			} else if (input instanceof Ingredient) {
				if (input == Ingredient.EMPTY)
					continue;

				ingredients.addIngredient(PEIApi.getIngredient((Ingredient) input), 1);
			} else if (input instanceof Object[]) {
				PEIApi.LOG.debug("Found Array within Array: {} within {}", input, inputs);
				getInputsSecond(ingredients, (Object[]) input);
			} else {
				PEIApi.LOG.warn("Unknown Input: {} ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
				continue;
			}
		}

		return ingredients;
	}

	public static IngredientMap<Object> getInputsSecond(IngredientMap<Object> ingredients, Object... inputs) {
		for (Object input : inputs) {
			if (input == null)
				continue;

			if (input instanceof ItemStack) {
				if (((ItemStack) input).isEmpty())
					continue;

				ingredients.addIngredient(input, ((ItemStack) input).getCount());
			} else if (input instanceof Item || input instanceof Block || input instanceof String
					|| input.getClass().equals(Object.class)) {
				ingredients.addIngredient(input, 1);
			} else if (input instanceof FluidStack) {
				if (((FluidStack) input).amount <= 0)
					continue;

				ingredients.addIngredient(input, ((FluidStack) input).amount);
			} else if (input instanceof List) {
				List<?> inputt = (List<?>) input;
				if (inputt.isEmpty())
					continue;

				if (inputt.size() == 1) {
					getInputsFirst(ingredients, inputt.get(0));
					continue;
				}

				ingredients.addIngredient(PEIApi.getList((List<?>) input), 1);
			} else if (input instanceof Ingredient) {
				if (input == Ingredient.EMPTY)
					continue;

				ingredients.addIngredient(PEIApi.getIngredient((Ingredient) input), 1);
			} else if (input instanceof Object[]) {
				PEIApi.LOG.debug("Found Array within Array: {} within {}", input, inputs);
				getInputsFirst(ingredients, Arrays.asList(input));
			} else {
				PEIApi.LOG.warn("Unknown Input: {} ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
				continue;
			}
		}

		return ingredients;
	}
}
