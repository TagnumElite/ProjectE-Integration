package com.tagnumelite.projecteintegration.api.mappers;

import java.util.Map;

import com.tagnumelite.projecteintegration.api.PEIApi;

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
	protected final IConversionProxy conversion_proxy;
	
	/**
	 * 
	 * @param name {@code String} The name of the recipe type
	 * @param description {@code String} The config comment
	 */
	public PEIMapper(String name, String description) {
		this.name = name;
		this.desc = description;
		this.conversion_proxy = ProjectEAPI.getConversionProxy();
	}
	
	/**
	 * You setup conversions and call {@code addConversion} here!
	 */
	public abstract void setup();
	
	/**
	 * This is a shortcut if the recipe extends IRecipe
	 * @param recipe {@code IRecipe} The recipe to convert
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
	
	/**
	 * Add Conversion for ItemStack
	 * @param item {@code ItemStack} The {@code ItemStack} to be processed
	 * @param input {@inheritDoc} The {@code Map<Object, Integer>} that contains the ingredients
	 */
	protected void addConversion(ItemStack item, Map<Object, Integer> input) {
		if (item == null || item.isEmpty())
			return; //TODO: Log Failed Item
		
		addConversion(item.getCount(), item, input);
	}
	
	/**
	 * Add Conversion for FluidStack
	 * @param fluid {@code FluidStack} the {@code FluidStack} to be processed
	 * @param input {@inheritDoc} The {@code Map} that contains the ingredients
	 */
	protected void addConversion(FluidStack fluid, Map<Object, Integer> input) {
		if (fluid == null || fluid.amount == 0)
			return; //TODO: Log Failed Fluid
		
		addConversion(fluid.amount, fluid, input);
	}
	
	/**
	 * This mimicks ProjectEApi IConversionProxy addConversion
	 * @param output_amount {@code int} The output amount
	 * @param output {@code Object} The output
	 * @param input {@code Map<Object, Integer>} The ingredient map
	 */
	protected void addConversion(int output_amount, Object output, Map<Object, Integer> input) {
		if (output_amount <= 0)
			return; //TODO: Log Failed output amount
		
		conversion_proxy.addConversion(output_amount, output, input);
	}
}
