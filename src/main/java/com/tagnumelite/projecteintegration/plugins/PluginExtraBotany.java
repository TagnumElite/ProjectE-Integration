package com.tagnumelite.projecteintegration.plugins;

import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.common.crafting.recipe.RecipePedestal;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid = "extrabotany")
public class PluginExtraBotany extends PEIPlugin {
	public PluginExtraBotany(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new PedestalMapper());
	}

	private class PedestalMapper extends PEIMapper {
		public PedestalMapper() {
			super("Pedestal", "");
		}

		@Override
		public void setup() {
			for (RecipePedestal recipe : ExtraBotanyAPI.pedestalRecipes) {
				addRecipe(recipe.getOutput(), recipe.getInput());
			}
		}
	}
}
