package com.tagnumelite.projecteintegration.plugins;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid="astralsorcery")
public class PluginAstralSorcery extends PEIPlugin {

	public PluginAstralSorcery(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setup() {
		addMapper(new AltarMapper());
		addMapper(new GrindstoneMapper());
		addMapper(new StarlightInfusionMapper());
	}
	
	private class AltarMapper extends PEIMapper {
		public AltarMapper() {
			super("Altar",
				  "");
		}

		@Override
		public void setup() {
			Set<AbstractAltarRecipe> recipes = new HashSet<>();
			AltarRecipeRegistry.recipes.values().forEach(recipes::addAll);
			AltarRecipeRegistry.mtRecipes.values().forEach(recipes::addAll);
			
			for (AbstractAltarRecipe recipe : recipes) {
				addRecipe(recipe.getNativeRecipe());
			}
		}
	}

	private class GrindstoneMapper extends PEIMapper {
		public GrindstoneMapper() {
			super("Grindstone",
				  "These recipes contain chances, so it is disabled by default",
				  true);
		}

		@Override
		public void setup() {
			Set<GrindstoneRecipe > recipes = new HashSet<>();
			recipes.addAll(GrindstoneRecipeRegistry.recipes);
			recipes.addAll(GrindstoneRecipeRegistry.mtRecipes);
			
			for (GrindstoneRecipe recipe : recipes) {
				ItemStack output = recipe.getOutputForMatching();
				if (output == null || output.isEmpty())
					continue;
				
				Object input = PEIApi.getList(recipe.getInputForRender().getApplicableItems());
				if (input == null)
					continue;

				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}
	
	private class StarlightInfusionMapper extends PEIMapper {
		public StarlightInfusionMapper() {
			super("Starlight Infusion",
			      "");
		}

		@Override
		public void setup() {
			Set<AbstractInfusionRecipe> recipes = new HashSet<>();
			recipes.addAll(InfusionRecipeRegistry.recipes);
			recipes.addAll(InfusionRecipeRegistry.mtRecipes);

			for (AbstractInfusionRecipe recipe : recipes) {
				ItemStack output = recipe.getOutputForMatching();
				if (output == null || output.isEmpty())
					continue;
				
				Object input = PEIApi.getList(recipe.getInput().getApplicableItems());
				if (input == null)
					continue;
				
				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}
}
