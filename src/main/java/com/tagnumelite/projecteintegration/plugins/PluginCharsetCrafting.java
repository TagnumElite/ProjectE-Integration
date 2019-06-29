package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import pl.asie.charset.lib.recipe.RecipeCharset;

@RegPEIPlugin(modid = "charset")
public class PluginCharsetCrafting extends PEIPlugin {
	public PluginCharsetCrafting(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new CharsetLibRecipeMapper());
	}
	
	private class CharsetLibRecipeMapper extends PEIMapper {
		public CharsetLibRecipeMapper() {
			super("Charset Lib Recipe");
		}

		@Override
		public void setup() {
			for (IRecipe recipe : CraftingManager.REGISTRY) {
				if (recipe instanceof RecipeCharset) {
					addRecipe(recipe);
				}
			}
		}
	}
}
