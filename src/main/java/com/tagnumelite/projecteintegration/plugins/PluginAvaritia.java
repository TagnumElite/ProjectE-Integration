package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="avaritia")
public class PluginAvaritia extends PEIPlugin {
	private final float compressor_cost_multiplier;

	public PluginAvaritia(String modid, Configuration config) {
		super(modid, config);
		
		this.compressor_cost_multiplier = config.getFloat("compressor_cost_multiplier", this.category, 1F, 0.00001F, 1F, "Mupltier to the EMC calulation");
	}
	
	@Override
	public void setup() {
		addEMC(ModItems.neutron_pile, 128);
		
		addMapper(new ExtremeMapper());
		addMapper(new CompressorMapper());
	}
	
	private class ExtremeMapper extends PEIMapper {

		public ExtremeMapper() {
			super("Extreme Crafting Table",
					"");
		}

		@Override
		public void setup() {
			for (IExtremeRecipe recipe : AvaritiaRecipeManager.EXTREME_RECIPES.values()) {
				ItemStack output = recipe.getRecipeOutput();
				if (output.isEmpty())
					continue;
				
				NonNullList<Ingredient> inputs = recipe.getIngredients();
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
	
				for (Ingredient input : inputs) {
					if (input == Ingredient.EMPTY)
						continue;
					
					ingredients.addIngredient(PEIApi.getIngredient(input), 1);
				}
				
				addConversion(output, ingredients.getMap());
			}
		}
		
	}
	
	private class CompressorMapper extends PEIMapper {

		public CompressorMapper() {
			super("Compressor",
					"");
		}

		@Override
		public void setup() {
			for (ICompressorRecipe recipe : AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()) {
				ItemStack output = recipe.getResult();
				if (output.isEmpty())
					continue;
				
				addConversion(output, ImmutableMap.of(PEIApi.getList(recipe.getIngredients()), Math.max(Math.round(recipe.getCost() * compressor_cost_multiplier), 1)));
			}
		}
	}
}
