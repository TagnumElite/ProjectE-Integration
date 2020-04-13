package com.tagnumelite.projecteintegration.addon.magic.plugins;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.recipe.TrickRecipe;

@PEIPlugin("psi")
public class PluginPsi extends APEIPlugin {
	public PluginPsi(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() throws Exception {
		addMapper(new TrickMapper());
	}

	private static class TrickMapper extends PEIMapper {
		public TrickMapper() {
			super("Trick");
		}

		@Override
		public void setup() {
			for (TrickRecipe recipe : PsiAPI.trickRecipes) {
				addRecipe(recipe.getOutput(), recipe.getInput());
			}
		}
	}
}
