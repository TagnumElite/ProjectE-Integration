package com.tagnumelite.projecteintegration.addon.plugins;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import techreborn.api.RollingMachineRecipe;

@PEIPlugin("techreborn")
public class PluginTechReborn extends APEIPlugin {
	public PluginTechReborn(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new RollingMachineMapper());
	}

	private static class RollingMachineMapper extends PEIMapper {
		public RollingMachineMapper() {
			super("Rolling Machine");
		}

		@Override
		public void setup() {
			for (IRecipe recipe : RollingMachineRecipe.instance.getRecipeList().values()) {
				addRecipe(recipe);
			}
		}
	}
}
