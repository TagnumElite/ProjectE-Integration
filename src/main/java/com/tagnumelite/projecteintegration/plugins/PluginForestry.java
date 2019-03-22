package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@RegPEIPlugin(modid="forestry")
public class PluginForestry extends PEIPlugin {
	public PluginForestry(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new CarpenterMapper());
		//addMapper(new CentrifugeMapper()); //INFO: Has multiple outputs, not doing for now 
		addMapper(new FabricatorMapper());
		addMapper(new FermenterMapper());
		addMapper(new MoistenerMapper());
		addMapper(new SqueezerMapper());
		addMapper(new StillMapper());
	}
	
	private class CarpenterMapper extends PEIMapper {
		public CarpenterMapper() {
			super("Carpenter", "");
		}

		@Override
		public void setup() {
			for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
				addRecipe(recipe.getBox(), recipe.getCraftingGridRecipe().getRawIngredients().toArray(), recipe.getFluidResource());
			}
		}
	}
	
	private class FabricatorMapper extends PEIMapper {
		public FabricatorMapper() {
			super("Fabricator", "");
		}

		@Override
		public void setup() {
			for (IFabricatorRecipe recipe : RecipeManagers.fabricatorManager.recipes()) {
				addRecipe(recipe.getRecipeOutput(), recipe.getPlan(), recipe.getLiquid(), recipe.getIngredients().toArray());
			} 
		}
	}
	
	private class FermenterMapper extends PEIMapper {
		public FermenterMapper() {
			super("Fermenter", "");
		}

		@Override
		public void setup() {
			for (IFermenterRecipe recipe : RecipeManagers.fermenterManager.recipes()) {
				addRecipe(new FluidStack(recipe.getOutput(), recipe.getFermentationValue()), recipe.getFluidResource(), recipe.getResource());
			}
		}
	}
	
	private class MoistenerMapper extends PEIMapper {
		public MoistenerMapper() {
			super("Moistener", "");
		}

		@Override
		public void setup() {
			for (IMoistenerRecipe recipe : RecipeManagers.moistenerManager.recipes()) {
				addRecipe(recipe.getProduct(), recipe.getResource());
			}
		}
	}
	
	private class SqueezerMapper extends PEIMapper {
		public SqueezerMapper() {
			super("Squeezer", "");
		}

		@Override
		public void setup() {
			for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
				addRecipe(recipe.getFluidOutput(), recipe.getResources());
			}
		}
	}
	
	private class StillMapper extends PEIMapper {
		public StillMapper() {
			super("Still", "");
		}

		@Override
		public void setup() {
			for (IStillRecipe recipe : RecipeManagers.stillManager.recipes()) {
				addRecipe(recipe.getOutput(), recipe.getInput());
			}
		}
	}
}
