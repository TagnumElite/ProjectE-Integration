package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import techreborn.api.RollingMachineRecipe;

@RegPEIPlugin(modid = "techreborn")
public class PluginTechReborn extends PEIPlugin {
	public PluginTechReborn(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new RollingMachineMapper());
	}

	private class RollingMachineMapper extends PEIMapper {
		public RollingMachineMapper() {
			super("Rolling Machine", "");
		}

		@Override
		public void setup() {
			for (IRecipe recipe : RollingMachineRecipe.instance.getRecipeList().values()) {
				addRecipe(recipe);
			}
		}
	}
}
