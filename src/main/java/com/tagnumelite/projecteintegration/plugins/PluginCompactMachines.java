package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.dave.compactmachines3.miniaturization.MultiblockRecipe;
import org.dave.compactmachines3.miniaturization.MultiblockRecipes;

@RegPEIPlugin(modid = "compactmachines3")
public class PluginCompactMachines extends PEIPlugin {
	public PluginCompactMachines(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new MiniaturizationMapper());
	}

	private class MiniaturizationMapper extends PEIMapper {
		public MiniaturizationMapper() {
			super("Miniaturization", "");
		}

		@Override
		public void setup() {
			for (MultiblockRecipe recipe : MultiblockRecipes.getRecipes()) {
				addRecipe(recipe.getTargetStack(), recipe.getCatalystStack(),
						recipe.getRequiredItemStacks().toArray(new ItemStack[0]));
			}
		}
	}
}
