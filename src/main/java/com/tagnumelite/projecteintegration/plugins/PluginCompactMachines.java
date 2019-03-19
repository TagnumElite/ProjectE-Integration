package com.tagnumelite.projecteintegration.plugins;

import java.util.List;

import org.dave.compactmachines3.miniaturization.MultiblockRecipe;
import org.dave.compactmachines3.miniaturization.MultiblockRecipes;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="compactmachines3")
public class PluginCompactMachines extends PEIPlugin {
	public PluginCompactMachines(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setup() {
		addMapper(new MiniaturizationMapper());
	}
	
	private class MiniaturizationMapper extends PEIMapper {
		public MiniaturizationMapper() {
			super("Miniaturization",
				  "");
		}

		@Override
		public void setup() {
			for (MultiblockRecipe recipe : MultiblockRecipes.getRecipes()) {
				ItemStack output = recipe.getTargetStack();
				if (output == null || output.isEmpty())
					continue;
				
				ItemStack catalyst = recipe.getCatalystStack();
				if (catalyst == null || catalyst.isEmpty())
					continue;
				
				List<ItemStack> inputs = recipe.getRequiredItemStacks();
				if (inputs == null || inputs.size() <= 0)
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				ingredients.addIngredient(catalyst, catalyst.getCount());
				inputs.forEach(input -> {ingredients.addIngredient(inputs, input.getCount());});
				
				addConversion(output, ingredients.getMap());
			}
		}
	}
}
