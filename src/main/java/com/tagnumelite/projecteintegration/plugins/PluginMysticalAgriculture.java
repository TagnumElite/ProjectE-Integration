package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import com.blakebr0.mysticalagriculture.crafting.ReprocessorManager;
import com.blakebr0.mysticalagriculture.crafting.ReprocessorRecipe;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIPlugin;

@RegPEIPlugin(modid="mysticalagriculture")
public class PluginMysticalAgriculture extends PEIPlugin {
	public PluginMysticalAgriculture(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setup() {
		addMapper(new SeedProcessorMapper());
	}
	
	private class SeedProcessorMapper extends PEIMapper {
		public SeedProcessorMapper() {
			super("Seed Processor",
				  "");
		}

		@Override
		public void setup() {
			for (ReprocessorRecipe recipe : ReprocessorManager.getRecipes()) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;
				
				ItemStack input = recipe.getInput();
				if (input == null || input.isEmpty())
					continue;
				
				addConversion(output, ImmutableMap.of(input, input.getCount()));
			}
		}
		
	}
}
