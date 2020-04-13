package com.tagnumelite.projecteintegration.addon.plugins;

import java.util.Collection;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

//import ic2.api.classic.recipe.ClassicRecipes;
//import ic2.api.classic.recipe.machine.IMachineRecipeList;
import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin("ic2")
public class PluginIC2 extends APEIPlugin {
	public PluginIC2(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new MachineMapper(Recipes.compressor, "Compressor"));
		addMapper(new MachineMapper(Recipes.extractor, "Extractor"));
		addMapper(new MachineMapper(Recipes.macerator, "Macerator"));
		// Check for IC2 Experimental
		try {
			Class.forName("ic2.core.recipe.AdvRecipe");

			addMapper(new AdvanceRecipeMapper());
			addMapper(new MachineMapper(Recipes.blastfurnace, "Blast Furnace"));
			addMapper(new MachineMapper(Recipes.blockcutter, "Block Cutter"));
			addMapper(new MachineMapper(Recipes.centrifuge, "Centrifuge"));
			// addMapper(new MachineMapper(Recipes.compressor));
			// addMapper(new MachineMapper(Recipes.extractor));
			// addMapper(new MachineMapper(Recipes.macerator));
			addMapper(new MachineMapper(Recipes.metalformerCutting, "Metal Former Cutting"));
			addMapper(new MachineMapper(Recipes.metalformerExtruding, "Metal Former Extruding"));
			addMapper(new MachineMapper(Recipes.metalformerRolling, "Metal Former Rolling"));
			addMapper(new MachineMapper(Recipes.oreWashing, "Ore Washing"));
		} catch (ClassNotFoundException e1) {
			/*PEIApi.LOG.info("IC2 Experimental not found, checking for IC2 Classic");
			try {
				Class.forName("ic2.api.classic.recipe.ClassicRecipes");

				addMapper(new ClassicRecipeMapper(ClassicRecipes.compressor, "Classic Compressor"));
				addMapper(new ClassicRecipeMapper(ClassicRecipes.extractor, "Classic Extractor"));
				addMapper(new ClassicRecipeMapper(ClassicRecipes.macerator, "Classic Macerator"));
				addMapper(new ClassicRecipeMapper(ClassicRecipes.sawMill, "Classic Saw Mill"));
				addMapper(new ClassicRecipeMapper(ClassicRecipes.furnace, "Classic Furnace"));
				addMapper(new ClassicRecipeMapper(ClassicRecipes.massfabAmplifier, "Classic Mass Fab Amplifier"));
			} catch (ClassNotFoundException e2) {
				PEIApi.LOG.error("IC2 Exp and IC2 Classic not found, this IC2 edition not recognised", e2);
			}*/
		}
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

	private static class MachineMapper extends PEIMapper {
		private final IBasicMachineRecipeManager manager;

		public MachineMapper(IBasicMachineRecipeManager manager, String name) {
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

	/*private static class ClassicRecipeMapper extends PEIMapper {
		private final IMachineRecipeList recipe_list;

		public ClassicRecipeMapper(IMachineRecipeList recipe_list, String name) {
			super(name);
			this.recipe_list = recipe_list;
		}

		@Override
		public void setup() {
			for (IMachineRecipeList.RecipeEntry recipe : recipe_list.getRecipeMap()) {
				recipe.getOutput();
				recipe.getInput().getInputs();

				addConversion(recipe.getOutput().getAllOutputs().get(0), ImmutableMap
						.of(PEIApi.getIngredient(recipe.getInput().getIngredient()), recipe.getInput().getAmount()));
			}
		}
	}*/
}
