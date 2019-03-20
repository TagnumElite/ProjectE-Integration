package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid = "actuallyadditions")
public class PluginActuallyAdditions extends PEIPlugin {
	public PluginActuallyAdditions(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addEMC(InitItems.itemMisc, 13, 64);
		addEMC(InitItems.itemCoffeeBean, 64);
		addEMC(InitItems.itemFoods, 16, 64);
		addEMC(InitItems.itemMisc, 15, 480);
		addEMC(InitItems.itemSolidifiedExperience, 863);

		addMapper(new EmpowererMapper());
		addMapper(new ReconstructorMapper());
		addMapper(new CrusherMapper());
	}

	private class EmpowererMapper extends PEIMapper {
		public EmpowererMapper() {
			super("Empowerer", "");
		}

		@Override
		public void setup() {
			for (EmpowererRecipe recipe : ActuallyAdditionsAPI.EMPOWERER_RECIPES) {
				addRecipe(recipe.getOutput(), recipe.getInput(), recipe.getStandOne(), recipe.getStandTwo(),
						recipe.getStandThree(), recipe.getStandFour());
			}
		}
	}

	private class ReconstructorMapper extends PEIMapper {
		public ReconstructorMapper() {
			super("Reconstructor", "");
		}

		@Override
		public void setup() {
			for (LensConversionRecipe recipe : ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES) {
				addRecipe(recipe.getOutput(), recipe.getInput());
			}
		}
	}

	private class CrusherMapper extends PEIMapper {
		public CrusherMapper() {
			super("crusher", "");
		}

		@Override
		public void setup() {
			for (CrusherRecipe recipe : ActuallyAdditionsAPI.CRUSHER_RECIPES) {
				addRecipe(recipe.getOutputOne(), recipe.getInput()); // TODO: Secondary Output
			}
		}
	}
}
