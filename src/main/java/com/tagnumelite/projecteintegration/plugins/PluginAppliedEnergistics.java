package com.tagnumelite.projecteintegration.plugins;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.InscriberProcessType;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="appliedenergistics2")
public class PluginAppliedEnergistics extends PEIPlugin {
	public PluginAppliedEnergistics(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setupIntegration() {
		addMapper(new InscriberMapper());
		addMapper(new GrindstoneMapper());
	}
	
	private class InscriberMapper extends PEIMapper {

		public InscriberMapper() {
			super("inscriber",
					"");
		}

		@Override
		public void setup() {
			for (IInscriberRecipe recipe : AEApi.instance().registries().inscriber().getRecipes()) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;
				
				ItemStack input = recipe.getInputs().get(0);
				if (input == null || input.isEmpty())
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				
				ingredients.addIngredient(input, input.getCount());
				
				if (recipe.getProcessType() == InscriberProcessType.PRESS) {
					Optional<ItemStack> input_top = recipe.getTopOptional();
					if (input_top.isPresent() && input_top.get() != ItemStack.EMPTY)
						ingredients.addIngredient(input_top.get(), input_top.get().getCount());
					
					Optional<ItemStack> input_bottom = recipe.getBottomOptional();
					if (input_bottom.isPresent() && input_bottom.get() != ItemStack.EMPTY)
						ingredients.addIngredient(input_bottom.get(), input_bottom.get().getCount());
				}
				
				addConversion(output.getCount(), output, ingredients.getMap());
			}
		}	
	}
	
	private class GrindstoneMapper extends PEIMapper {

		public GrindstoneMapper() {
			super("grindstone",
					"");
		}

		@Override
		public void setup() {
			for (IGrinderRecipe recipe : AEApi.instance().registries().grinder().getRecipes()) {
				ItemStack output = recipe.getOutput();
				ItemStack input = recipe.getInput();
				if (output.isEmpty() || input.isEmpty())
					continue;
				
				addConversion(output.getCount(), output, ImmutableMap.of((Object) input, input.getCount()));
			}
		}
		
	}
}
