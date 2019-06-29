package com.tagnumelite.projecteintegration.plugins;

import java.util.List;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

@RegPEIPlugin(modid="libVulpes")
public class PluginLibVulpes extends PEIPlugin {
	public PluginLibVulpes(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup(){
		PEIApi.LOG.debug("RECIPE LIST SIZE: %i", RecipesMachine.getInstance().recipeList.size());
		for (Class<? extends TileMultiblockMachine> clazz : RecipesMachine.getInstance().recipeList.keySet()) {
			PEIApi.LOG.debug("Adding new mapper from libvuples");
			addMapper(new RecipeMapper(clazz));
		}
	}

	@SuppressWarnings("rawtypes")
	protected static class RecipeMapper extends PEIMapper {
		private final Class clazz;

		public RecipeMapper(Class clazz) {
			super(clazz.getSimpleName());
			this.clazz = clazz;
		}

		@Override
		public void setup() {
			for (IRecipe recipe : RecipesMachine.getInstance().getRecipes(clazz)) {
				List<ItemStack> item_outputs = recipe.getOutput();
				List<FluidStack> fluid_outputs = recipe.getFluidOutputs();
				final boolean no_item_out = (item_outputs == null) || (item_outputs.isEmpty());
				final boolean no_fluid_out = (fluid_outputs == null) || (fluid_outputs.isEmpty());
				if (no_item_out || no_fluid_out)
					continue;
				
				if (!no_item_out) {
					for (ItemStack item : item_outputs) {
						addRecipe(item, recipe.getFluidIngredients().toArray(), recipe.getIngredients().toArray());
					}
				}
				
				if (!no_fluid_out) {
					for (FluidStack fluid : fluid_outputs) {
						addRecipe(fluid, recipe.getFluidIngredients().toArray(), recipe.getIngredients().toArray());
					}
				}
			}
		}
	}
}
