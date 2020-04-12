package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.internal.input.SizedIngredient;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import gregtech.api.recipes.CountableIngredient;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.Recipe.ChanceEntry;
import gregtech.api.recipes.RecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin("gregtech")
public class PluginGregTechCE extends APEIPlugin {
	public PluginGregTechCE(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (RecipeMap<?> map : RecipeMap.getRecipeMaps()) {
			addMapper(new RecipeMapper(map));
		}
	}
	
	private class RecipeMapper extends PEIMapper {
		private final RecipeMap<?> map;

		public RecipeMapper(RecipeMap<?> map) {
			super(map.unlocalizedName);
			this.map = map;
		}

		@Override
		public void setup() {
			for (Recipe recipe : map.getRecipeList()) {
				ArrayList<SizedIngredient> inputs = new ArrayList<>();
				for (CountableIngredient input : recipe.getInputs()) {
					inputs.add(new SizedIngredient(input.getCount(), input.getIngredient()));
				}
				
				ArrayList<Object> outputs = new ArrayList<>();
				
				for (FluidStack fluid : recipe.getFluidOutputs()) {
					outputs.add(fluid);
				}
				
				for (ItemStack output : recipe.getOutputs()) {
					outputs.add(output);
				}
				
				for (ChanceEntry output : recipe.getChancedOutputs()) {
					if (output.getChance() >= 20000) {
						ItemStack item = output.getItemStack().copy();
						item.setCount(item.getCount() * 2);
						outputs.add(item);
					} else if (output.getChance() >= 10000) {
						outputs.add(output.getItemStack());
					}
				}
				
				addRecipe(outputs, inputs.toArray(), recipe.getFluidInputs().toArray());
			}
		}
	}
}
