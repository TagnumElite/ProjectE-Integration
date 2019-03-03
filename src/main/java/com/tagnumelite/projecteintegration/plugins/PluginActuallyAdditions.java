package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="actuallyadditions")
public class PluginActuallyAdditions extends PEIPlugin {
	public PluginActuallyAdditions(String modid, Configuration config) {
		super(modid, config);
	}
	
	@Override
	public void setupIntegration() {
		addEMC(InitItems.itemMisc, 13, 64);
		addEMC(InitItems.itemCoffeeBean, 64);
		addEMC(InitItems.itemFoods, 16, 64);
		addEMC(InitItems.itemMisc, 15, 480);
		addEMC(InitItems.itemSolidifiedExperience, 863);
		
		addMapper(new EmpowererMapper());
		addMapper(new ReconstructorMapper());
		addMapper(new CrusherMapper());
	}
	
	private class EmpowererMapper extends PEIMapper {
		public EmpowererMapper() {
			super("empowerer",
				  "");
		}

		@Override
		public void setup() {
			for (EmpowererRecipe recipe : ActuallyAdditionsAPI.EMPOWERER_RECIPES) {
				ItemStack output = recipe.getOutput();
				if (output.isEmpty())
					continue;
				Ingredient input = recipe.getInput();
				if (input == Ingredient.EMPTY)
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				ingredients.addIngredient(PEIApi.getIngredient(input), 1);
				
				Ingredient stand1 = recipe.getStandOne();
				if (stand1 != Ingredient.EMPTY)
					ingredients.addIngredient(PEIApi.getIngredient(stand1), 1);
				Ingredient stand2 = recipe.getStandTwo();
				if (stand2 != Ingredient.EMPTY)
					ingredients.addIngredient(PEIApi.getIngredient(stand2), 1);
				Ingredient stand3 = recipe.getStandThree();
				if (stand3 != Ingredient.EMPTY)
					ingredients.addIngredient(PEIApi.getIngredient(stand3), 1);
				Ingredient stand4 = recipe.getStandFour();
				if (stand4 != Ingredient.EMPTY)
					ingredients.addIngredient(PEIApi.getIngredient(stand4), 1);
				
				addConversion(output.getCount(), output, ingredients.getMap());
			}
		}
	}
		
	private class ReconstructorMapper extends PEIMapper {

		public ReconstructorMapper() {
			super("reconstructor",
				  "");
		}

		@Override
		public void setup() {
			for (LensConversionRecipe recipe : ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;
				
				Ingredient input = recipe.getInput();
				if (input == null || input == Ingredient.EMPTY)
					continue;
				
				addConversion(output.getCount(), output, ImmutableMap.of(PEIApi.getIngredient(input), 1));
			}
		}
		
	}
	
	private class CrusherMapper extends PEIMapper {
	
		public CrusherMapper() {
			super("crusher",
				  "");
		}
	
		@Override
		public void setup() {
			for (CrusherRecipe recipe : ActuallyAdditionsAPI.CRUSHER_RECIPES) {
				ItemStack output = recipe.getOutputOne();
				if (output == null || output.isEmpty())
					continue;
				
				Ingredient input = recipe.getInput();
				if (input == null || input == Ingredient.EMPTY)
					continue;
				
				ImmutableMap<Object, Integer> map = ImmutableMap.of(PEIApi.getIngredient(input), 1);
				
				addConversion(output.getCount(), output, map);
				
				ItemStack output_2 = recipe.getOutputTwo();
				
				if (output_2 == null || output_2.isEmpty() || recipe.getSecondChance() < 100)
					continue;
				
				addConversion(output_2.getCount(), output_2, map);
			}
		}
		
	}
}
