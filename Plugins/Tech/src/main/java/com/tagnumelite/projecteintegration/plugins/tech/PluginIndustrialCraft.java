package com.tagnumelite.projecteintegration.plugins.tech;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import ic2.api.recipe.*;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Collection;

@PEIPlugin("ic2")
public class PluginIndustrialCraft extends APEIPlugin {
	public PluginIndustrialCraft(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
        addMapper(new AdvanceRecipeMapper());
        addMapper(new BasicMachineMapper(Recipes.compressor, "Compressor"));
        addMapper(new BasicMachineMapper(Recipes.extractor, "Extractor"));
        addMapper(new BasicMachineMapper(Recipes.macerator, "Macerator"));
        addMapper(new BasicMachineMapper(Recipes.blastfurnace, "Blast Furnace"));
        addMapper(new BasicMachineMapper(Recipes.blockcutter, "Block Cutter"));
        addMapper(new BasicMachineMapper(Recipes.centrifuge, "Centrifuge"));
        addMapper(new BasicMachineMapper(Recipes.metalformerCutting, "Metal Former Cutting"));
        addMapper(new BasicMachineMapper(Recipes.metalformerExtruding, "Metal Former Extruding"));
        addMapper(new BasicMachineMapper(Recipes.metalformerRolling, "Metal Former Rolling"));
        addMapper(new BasicMachineMapper(Recipes.oreWashing, "Ore Washing"));
        addMapper(new BasicMachineMapper(Recipes.recycler, "Recycler"));
	}

	private static class AdvanceRecipeMapper extends PEIMapper {
		public AdvanceRecipeMapper() {
			super("Advance Recipe");
		}

		@Override
		public void setup() {
			for (IRecipe recipe : CraftingManager.REGISTRY) {
				if (recipe instanceof AdvRecipe || recipe instanceof AdvShapelessRecipe) {
					addRecipe(recipe);
				}
			}
		}
	}

	private static class BasicMachineMapper extends PEIMapper {
		private final IBasicMachineRecipeManager manager;

		public BasicMachineMapper(IBasicMachineRecipeManager manager, String name) {
			super(name);
			this.manager = manager;
		}

		@Override
		public void setup() {
			for (MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe : manager.getRecipes()) {
				PEIApi.LOG.debug("IC2 Recipe: {} from {}*{}", recipe.getOutput(), recipe.getInput().getInputs(),
						recipe.getInput().getAmount());
				addConversion(recipe.getOutput().stream().findFirst().orElse(ItemStack.EMPTY), ImmutableMap
						.of(PEIApi.getIngredient(recipe.getInput().getIngredient()), recipe.getInput().getAmount()));
			}
		}
	}
}
