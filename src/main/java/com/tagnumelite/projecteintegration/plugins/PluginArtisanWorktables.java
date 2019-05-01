package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.List;

import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.IArtisanIngredient;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.OutputWeightPair;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktablesConfig;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.internal.SizedIngredient;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;

@RegPEIPlugin(modid = "artisanworktables")
public class PluginArtisanWorktables extends PEIPlugin {
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
			super(name + '_' + tier.getName(), "");
			this.name = name;
			this.tier = tier;
		}

		@Override
		public void setup() {
			List<IArtisanRecipe> recipe_list =  new ArrayList<>();
			recipe_list = ArtisanAPI.getWorktableRecipeRegistry(name).getRecipeListByTier(tier, recipe_list);
			
			for (IArtisanRecipe recipe : recipe_list) {
				List<IArtisanIngredient> a_ingredients = recipe.getIngredientList();
				List<SizedIngredient> ingredients = new ArrayList<>(a_ingredients.size());
				a_ingredients.forEach(ing -> {ingredients.add(new SizedIngredient(ing.getAmount(), ing.toIngredient()));});
				
				for (OutputWeightPair output : recipe.getOutputWeightPairList()) {
					addRecipe(output.getOutput().toItemStack(), recipe.getFluidIngredient(), ingredients.toArray());
				}
			}
		}
	}
}
