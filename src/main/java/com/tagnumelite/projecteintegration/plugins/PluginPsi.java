package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.recipe.TrickRecipe;

@RegPEIPlugin(modid = "psi")
public class PluginPsi extends PEIPlugin {
	public PluginPsi(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() throws Exception {
		addMapper(new TrickMapper());
	}

	private class TrickMapper extends PEIMapper {
		public TrickMapper() {
			super("Trick", "");
		}

		@Override
		public void setup() {
			for (TrickRecipe recipe : PsiAPI.trickRecipes) {
				addRecipe(recipe.getOutput(), recipe.getInput());
			}
		}
	}
}
