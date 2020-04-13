package com.tagnumelite.projecteintegration.addon.misc.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin("avaritia")
public class PluginAvaritia extends APEIPlugin {
	private final float compressor_cost_multiplier;

	public PluginAvaritia(String modid, Configuration config) {
		super(modid, config);

		this.compressor_cost_multiplier = config.getFloat("compressor_cost_multiplier", this.category, 1F, 0.00001F, 1F,
				"Multiplier to the EMC calculation");
	}

	@Override
	public void setup() {
		addEMC(ModItems.neutron_pile, 128);

		addMapper(new ExtremeMapper());
		addMapper(new CompressorMapper());
	}

	private static class ExtremeMapper extends PEIMapper {
		public ExtremeMapper() {
			super("Extreme Crafting Table");
		}

		@Override
		public void setup() {
			for (IExtremeRecipe recipe : AvaritiaRecipeManager.EXTREME_RECIPES.values()) {
				addRecipe(recipe.getRecipeOutput(), recipe.getIngredients().toArray());
			}
		}
	}

	private class CompressorMapper extends PEIMapper {
		public CompressorMapper() {
			super("Compressor");
		}

		@Override
		public void setup() {
			for (ICompressorRecipe recipe : AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()) {
				ItemStack output = recipe.getResult();
				if (output.isEmpty())
					continue;

				addConversion(output, ImmutableMap.of(PEIApi.getList(recipe.getIngredients()),
						Math.max(Math.round(recipe.getCost() * compressor_cost_multiplier), 1)));
			}
		}
	}
}
