package com.tagnumelite.projecteintegration.addon.misc.plugins;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import ipsis.Woot;
import ipsis.woot.crafting.IAnvilRecipe;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin("woot")
public class PluginWoot extends APEIPlugin {
	public PluginWoot(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() throws Exception {
		addMapper(new AnvilMapper());
	}

	private static class AnvilMapper extends PEIMapper {
		public AnvilMapper() {
			super("Anvil");
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
