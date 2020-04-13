package com.tagnumelite.projecteintegration.addon.misc.plugins;

import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

@PEIPlugin("tconstruct")
public class PluginTConstruct extends APEIPlugin {
	public PluginTConstruct(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new AlloyMapper());
		addMapper(new DryingMapper());
		addMapper(new MeltingMapper());
	}

	private static class AlloyMapper extends PEIMapper {
		public AlloyMapper() {
			super("Alloy", "Tinkers Smelter Alloying recipe support");
		}

		@Override
		public void setup() {
			for (AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
				addRecipe(recipe.getResult(), recipe.getFluids().toArray());
			}
		}
	}

	private static class DryingMapper extends PEIMapper {
		public DryingMapper() {
			super("Drying", "");
		}

		@Override
		public void setup() {
			for (DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
				addRecipe(recipe.getResult(), recipe.input.getInputs());
			}
		}
	}

	private static class MeltingMapper extends PEIMapper {
		public MeltingMapper() {
			super("Melting", "");
		}

		@Override
		public void setup() {
			for (MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
				addRecipe(recipe.getResult(), recipe.input.getInputs());
			}
		}
	}
}
