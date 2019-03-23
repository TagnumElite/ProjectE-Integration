package com.tagnumelite.projecteintegration.plugins;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid = "ic2")
public class PluginIC2 extends PEIPlugin {
	public PluginIC2(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new BasicMachineMapper(Recipes.blastfurnace));
		addMapper(new BasicMachineMapper(Recipes.blockcutter));
		addMapper(new BasicMachineMapper(Recipes.centrifuge));
		addMapper(new BasicMachineMapper(Recipes.compressor));
		addMapper(new BasicMachineMapper(Recipes.extractor));
		addMapper(new BasicMachineMapper(Recipes.macerator));
		addMapper(new BasicMachineMapper(Recipes.metalformerCutting));
		addMapper(new BasicMachineMapper(Recipes.metalformerExtruding));
		addMapper(new BasicMachineMapper(Recipes.metalformerRolling));
		addMapper(new BasicMachineMapper(Recipes.oreWashing));
	}

	private class BasicMachineMapper extends PEIMapper {
		private final IBasicMachineRecipeManager manager;

		public BasicMachineMapper(IBasicMachineRecipeManager manager) {
			super(manager.toString(), "");
			this.manager = manager;
		}

		@Override
		public void setup() {
			for (MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe : manager.getRecipes()) {
				addConversion(recipe.getOutput().stream().findFirst().orElse(ItemStack.EMPTY), ImmutableMap
						.of(PEIApi.getIngredient(recipe.getInput().getIngredient()), recipe.getInput().getAmount()));
			}
		}
	}
}
