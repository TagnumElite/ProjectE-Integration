package com.tagnumelite.projecteintegration.api.mappers;

import com.tagnumelite.projecteintegration.api.PEIApi;

import java.util.List;
import java.util.Map;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public abstract class PEIMapper {
	public final String name;
	public final String desc;
	public final boolean disabled_by_default;
	protected final IConversionProxy conversion_proxy;

	/**
	 * @param name
	 *            {@code String} The name of the recipe type
	 * @param description
	 *            {@code String} The config comment
	 */
	public PEIMapper(String name, String description) {
		this(name, description, false);
	}

	/**
	 * @param name
	 *            {@code String} The name of the recipe type
	 * @param description
	 *            {@code String} The config comment
	 * @param disableByDefault
	 *            {@code boolean} Disable by default
	 */
	protected PEIMapper(String name, String description, boolean disableByDefault) {
		this.name = name;
		this.desc = description;
		this.disabled_by_default = disableByDefault;
		this.conversion_proxy = ProjectEAPI.getConversionProxy();
	}

	/** You setup conversions and call {@code addConversion} here! */
	public abstract void setup();

	/**
	 * This is a shortcut if the recipe extends IRecipe
	 *
	 * @param recipe
	 *            {@code IRecipe} The recipe to convert
	 */
	protected void addRecipe(IRecipe recipe) {
		ItemStack output = recipe.getRecipeOutput();
		if (output == null || output.isEmpty())
			return;

		IngredientMap<Object> ingredients = new IngredientMap<Object>();

		for (Ingredient ingredient : recipe.getIngredients()) {
			if (ingredient == Ingredient.EMPTY)
				continue;

			ingredients.addIngredient(PEIApi.getIngredient(ingredient), 1);
		}

		addConversion(output, ingredients.getMap());
	}

	protected void addRecipe(ItemStack output, Object... inputs) {
		if (output == null || output.isEmpty())
			return;

		addRecipe(output.getCount(), output, inputs);
	}

	protected void addRecipe(FluidStack output, Object... inputs) {
		if (output == null || output.amount <= 0)
			return;

		addRecipe(output.amount, output, inputs);
	}

	protected void addRecipe(int output_amount, Object output, Object... inputs) {
		if (output_amount <= 0 || output == null || inputs == null || inputs.length <= 0)
			return;

		IngredientMap<Object> ingredients = new IngredientMap<Object>();

		for (Object input : inputs) {
			if (input == null)
				continue;

			if (input instanceof ItemStack) {
				if (((ItemStack) input).isEmpty())
					continue;

				ingredients.addIngredient(input, ((ItemStack) input).getCount());
			} else if (input instanceof Item || input instanceof Block || input instanceof String) {
				ingredients.addIngredient(input, 1);
			} else if (input instanceof FluidStack) {
				if (((FluidStack) input).amount <= 0)
					continue;
				ingredients.addIngredient(input, ((FluidStack) input).amount);
			} else if (input instanceof List) {
				if (((List<?>) input).isEmpty())
					continue;

				ingredients.addIngredient(PEIApi.getList((List<?>) input), 1);
			} else if (input instanceof Ingredient) {
				if (input == Ingredient.EMPTY)
					continue;

				ingredients.addIngredient(PEIApi.getIngredient((Ingredient) input), 1);
			} else {
				continue; // TODO: Log Unknown Item
			}
		}

		Map<Object, Integer> map = ingredients.getMap();

		if (map == null || map.isEmpty())
			return;

		addConversion(output_amount, output, map);
	}

	/**
	 * Add Conversion for ItemStack
	 *
	 * @param item
	 *            {@code ItemStack} The {@code ItemStack} to be processed
	 * @param input
	 *            The {@code Map<Object, Integer>} that contains the ingredients
	 */
	protected void addConversion(ItemStack item, Map<Object, Integer> input) {
		if (item == null || item.isEmpty())
			return; // TODO: Log Failed Item

		addConversion(item.getCount(), item, input);
	}

	/**
	 * Add Conversion for FluidStack
	 *
	 * @param fluid
	 *            {@code FluidStack} the {@code FluidStack} to be processed
	 * @param input
	 *            The {@code Map} that contains the ingredients
	 */
	protected void addConversion(FluidStack fluid, Map<Object, Integer> input) {
		if (fluid == null || fluid.amount == 0)
			return; // TODO: Log Failed Fluid

		addConversion(fluid.amount, fluid, input);
	}

	/**
	 * This mimicks ProjectEApi IConversionProxy addConversion
	 *
	 * @param output_amount
	 *            {@code int} The output amount
	 * @param output
	 *            {@code Object} The output
	 * @param input
	 *            {@code Map<Object, Integer>} The ingredient map
	 */
	protected void addConversion(int output_amount, Object output, Map<Object, Integer> input) {
		if (output_amount <= 0 || output == null)
			return; // TODO: Log Failed output amount

		conversion_proxy.addConversion(output_amount, output, input);
	}
}
