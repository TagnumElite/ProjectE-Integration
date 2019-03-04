package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.utils.ConfigHelper;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.RecipeTranslator;

@RegPEIPlugin(modid="reborncore")
public class PluginRebornCore extends PEIPlugin {

	public PluginRebornCore(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setupIntegration() {
		addMapper(new RebornCoreMapper());
	}
	
	private class RebornCoreMapper extends PEIMapper {
		private final Map<String, Boolean> RECIPE_CFG_MAP = new HashMap<>();
		
		public RebornCoreMapper() {
			super("Reborn Core",
				  "");
		}

		@Override
		public void setup() {
			for (IBaseRecipeType recipe : RecipeHandler.recipeList) {
				String recipe_name = recipe.getRecipeName();
				if (RECIPE_CFG_MAP.containsKey(recipe_name)) {
					if (!RECIPE_CFG_MAP.get(recipe_name))
						continue;
				} else {
					RECIPE_CFG_MAP.put(recipe_name,
							config.getBoolean(ConfigHelper.getConversionName(recipe_name), category, true, "Enable conversions for machine " + recipe_name));
				}
				
				List<ItemStack> outputs = recipe.getOutputs();
				if (outputs == null || outputs.isEmpty())
					continue;
				
				List<Object> inputs = recipe.getInputs();
				if (inputs == null || inputs.isEmpty())
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				
				inputs.forEach(input -> {
					ItemStack item = RecipeTranslator.getStackFromObject(input);
					ingredients.addIngredient(item, item.getCount());
				});
				
				Map<Object, Integer> map = ingredients.getMap();
				if (map == null || map.isEmpty())
					continue;
				
				outputs.forEach(output -> {
					if (output != null && !output.isEmpty())
						addConversion(output, map);
				});
			}
			
			RECIPE_CFG_MAP.clear();
		}
	}
}

