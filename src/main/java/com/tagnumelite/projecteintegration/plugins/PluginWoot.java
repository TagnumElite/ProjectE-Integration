package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import ipsis.Woot;
import ipsis.woot.crafting.IAnvilRecipe;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid = "woot")
public class PluginWoot extends PEIPlugin {
	public PluginWoot(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() throws Exception {
		addMapper(new AnvilMapper());
	}

	private class AnvilMapper extends PEIMapper {
		public AnvilMapper() {
			super("Anvil", "");
		}

		@Override
		public void setup() {
			for (IAnvilRecipe recipe : Woot.anvilManager.getRecipes()) {
				if (recipe.shouldPreserveBase()) {
					addRecipe(recipe.getCopyOutput(), recipe.getInputs().toArray());
				} else {
					addRecipe(recipe.getCopyOutput(), recipe.getBaseItem(), recipe.getInputs().toArray());
				}
			}
		}
	}
}
