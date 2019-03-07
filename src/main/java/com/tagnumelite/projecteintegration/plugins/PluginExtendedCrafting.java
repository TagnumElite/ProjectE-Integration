package com.tagnumelite.projecteintegration.plugins;

import java.util.List;

import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="extendedcrafting")
public class PluginExtendedCrafting extends PEIPlugin {
	public PluginExtendedCrafting(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new ECCompressorMapper());
		addMapper(new ECCombinationMapper());
		addMapper(new ECTableMapper());
	}
	
	private class ECCompressorMapper extends PEIMapper {
		public ECCompressorMapper() {
			super("compressor",
				  "Enable Quantum Compressor recipe mapper?");
		}

		@Override
		public void setup() {
			for (CompressorRecipe recipe : CompressorRecipeManager.getInstance().getRecipes()) {
				ItemStack output = recipe.getOutput();
				if (output.isEmpty())
					continue;
				
				Object input = recipe.getInput();
				if (input == null)
					continue;
				
				if (input instanceof List<?>) {
					if (((List<?>) input).isEmpty())
						continue;
					
					input = PEIApi.getList((List<?>) input);
					/*Object input_l = PEIApi.getList((List<?>) input);
					if (input_l == null)
						continue;
					
					input = input_l;*/
				}
					
				addConversion(output.getCount(), output, ImmutableMap.of(input, recipe.getInputCount()));
			}
		}
	}
	
	private class ECCombinationMapper extends PEIMapper {
		public ECCombinationMapper() {
			super("combination",
				  "Enable Conbination crafting recipe mapper?");
		}

		@Override
		public void setup() {
			for (CombinationRecipe recipe : CombinationRecipeManager.getInstance().getRecipes()) {
				ItemStack output = recipe.getOutput();
				ItemStack input = recipe.getInput();
				if (output.isEmpty() || input.isEmpty())
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				
				ingredients.addIngredient(input, input.getCount());
				
				for (Object pedestal : recipe.getPedestalItems()) {
					if (pedestal instanceof NonNullList<?>)
						ingredients.addIngredient(PEIApi.getList((List<?>) pedestal), 1);
					else
						ingredients.addIngredient(pedestal, 1);
				}
				
				addConversion(output.getCount(), output, ingredients.getMap());
			}
		}
	}
	
	private class ECTableMapper extends PEIMapper {
		public ECTableMapper() {
			super("table",
				  "Enable Ender and Tiered crafting recipe mapper?");
		}

		@Override
		public void setup() {
			for (Object recipe : EnderCrafterRecipeManager.getInstance().getRecipes()) {
				addRecipe((IRecipe) recipe);
			}
			
			for (Object recipe : TableRecipeManager.getInstance().getRecipes()) {
				addRecipe((IRecipe) recipe);
			}
		}
	}
}
