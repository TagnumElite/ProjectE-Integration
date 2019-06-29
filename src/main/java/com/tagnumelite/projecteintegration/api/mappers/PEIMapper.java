package com.tagnumelite.projecteintegration.api.mappers;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.utils.Utils;

import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
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
	
	public PEIMapper(String name, boolean disabledByDefault) {
		this(name, "Enable mapper for " + name + '?', disabledByDefault);
	}
	
	public PEIMapper(String name) {
		this(name, "Enable mapper for " + name + '?');
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
			return; // TODO: Logging

		addRecipe(output.getCount(), output.copy(), inputs);
	}

	protected void addRecipe(FluidStack output, Object... inputs) {
		if (output == null || output.amount <= 0)
			return;

		addRecipe(output.amount, output.copy(), inputs);
	}

	protected void addRecipe(int output_amount, Object output, Object... inputs) {
		if (output_amount <= 0 || output == null || inputs == null || inputs.length <= 0) {
			return; // TODO: Logging
		}

		addConversion(output_amount, output, Utils.createInputs(inputs).getMap());
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
		if (item == null || item.isEmpty()) {
			PEIApi.LOG.warn("Output Item is either null or Empty: {} from {}", item, input);
			return;
		}

		addConversion(item.getCount(), item.copy(), input);
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
		if (fluid == null || fluid.amount == 0) {
			PEIApi.LOG.warn("Output Fluid is either null or Empty: {} from {}", fluid, input);
			return;
		}

		addConversion(fluid.amount, fluid.copy(), input);
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
		if (output_amount <= 0 || output == null || input == null || input.isEmpty()) {
			Object output_l = output;
			if (output instanceof FluidStack)
				output_l = ((FluidStack) output).getFluid().getName();

			PEIApi.LOG.warn("Invalid Conversion: [{} ({})]*{} from {}", output_l,
					ClassUtils.getPackageCanonicalName(output.getClass()), output_amount, input);
			return;
		}

		try {
			conversion_proxy.addConversion(output_amount, output, input);
			PEIApi.mapped_conversions += 1;
		} catch (Exception e) {
			PEIApi.LOG.error("Failed to add conversion: {}*{} from {}; {}", output, output_amount, input, e);
		}
	}
}
