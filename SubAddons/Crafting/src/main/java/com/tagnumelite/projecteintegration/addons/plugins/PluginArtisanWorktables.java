package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.List;

import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.OutputWeightPair;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktablesConfig;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.internal.input.SizedIngredient;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin( "artisanworktables")
public class PluginArtisanWorktables extends APEIPlugin {
	public PluginArtisanWorktables(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (EnumTier tier : EnumTier.values()) {
			if (!ModuleWorktablesConfig.isTierEnabled(tier)) {
				for (String name : ArtisanAPI.getWorktableNames()) {
					addMapper(new WorktableMapper(name, tier));
				}
			}
		}
	}
	
	private class WorktableMapper extends PEIMapper {
		private final String name;
		private final EnumTier tier;

		public WorktableMapper(String name, EnumTier tier) {
			super(name + '_' + tier.getName(), "Enable mapper for worktable " + name + ' ' + tier.getName() + '?');
			this.name = name;
			this.tier = tier;
		}

		@Override
		public void setup() {
			List<IArtisanRecipe> recipe_list =  new ArrayList<>();
			recipe_list = ArtisanAPI.getWorktableRecipeRegistry(name).getRecipeListByTier(tier, recipe_list);
			
			
			for (IArtisanRecipe recipe : recipe_list) {
				List<SizedIngredient> ingredients = new ArrayList<>();
				recipe.getIngredientList().forEach(ing -> {ingredients.add(new SizedIngredient(ing.getAmount(), ing.toIngredient()));});
				recipe.getSecondaryIngredients().forEach(ing -> {ingredients.add(new SizedIngredient(ing.getAmount(), ing.toIngredient()));});
				FluidStack fluid_input = recipe.getFluidIngredient();
				
				ArrayList<Object> outputs = new ArrayList<>();
				for (OutputWeightPair output : recipe.getOutputWeightPairList()) {
					outputs.add(output.getOutput().toItemStack());
					//addRecipe(output.getOutput().toItemStack(), fluid_input, ingredients.toArray());
				}
				
				if (recipe.getSecondaryOutputChance() >= 1f) {
					outputs.add(recipe.getSecondaryOutput().toItemStack());
					//addRecipe(recipe.getSecondaryOutput().toItemStack(), fluid_input, ingredients.toArray());
				}
				
				if (recipe.getTertiaryOutputChance() >= 1f) {
					outputs.add(recipe.getTertiaryOutput().toItemStack());
					//addRecipe(recipe.getTertiaryOutput().toItemStack(), fluid_input, ingredients.toArray());
				}
				
				if (recipe.getQuaternaryOutputChance() >= 1f) {
					outputs.add(recipe.getQuaternaryOutput().toItemStack());
					//addRecipe(recipe.getQuaternaryOutput().toItemStack(), fluid_input, ingredients.toArray());
				}
				
				addRecipe(outputs, fluid_input, ingredients.toArray());
			}
		}
	}
}
