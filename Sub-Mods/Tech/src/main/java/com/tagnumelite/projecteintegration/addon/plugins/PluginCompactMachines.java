package com.tagnumelite.projecteintegration.addon.plugins;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.dave.compactmachines3.miniaturization.MultiblockRecipe;
import org.dave.compactmachines3.miniaturization.MultiblockRecipes;

@PEIPlugin("compactmachines3")
public class PluginCompactMachines extends APEIPlugin {
	public PluginCompactMachines(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new MiniaturizationMapper());
	}

	private static class MiniaturizationMapper extends PEIMapper {
		public MiniaturizationMapper() {
			super("Miniaturization");
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
