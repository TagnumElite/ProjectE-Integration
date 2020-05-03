package com.tagnumelite.projecteintegration.plugins.crafting;

import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.api.internal.recipe.OutputWeightPair;
import com.codetaylor.mc.artisanworktables.api.internal.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktablesConfig;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

@PEIPlugin("artisanworktables")
public class PluginArtisanWorktables extends APEIPlugin {
    public PluginArtisanWorktables(String modid, Configuration config) {
        super(modid, config);
    }

    @Override
    public void setup() {
        for (String name : ArtisanAPI.getWorktableNames()) {
            for (EnumTier tier : EnumTier.values()) {
                if (!ModuleWorktablesConfig.isTierEnabled(tier)) {
                    addMapper(new WorktableMapper(name, tier));
                }
            }
        }
    }

    private static class WorktableMapper extends PEIMapper {
        private final String name;
        private final EnumTier tier;

        public WorktableMapper(String name, EnumTier tier) {
            super(name + ' ' + tier.getName(), "Enable mapper for worktable " + name + ' ' + tier.getName() + '?');
            this.name = name;
            this.tier = tier;
        }

        @Override
        public void setup() {
            List<IArtisanRecipe> recipe_list = new ArrayList<>();
            recipe_list = ArtisanAPI.getWorktableRecipeRegistry(name).getRecipeListByTier(tier, recipe_list);

            for (IArtisanRecipe recipe : recipe_list) {
                List<SizedObject<Object>> ingredients = new ArrayList<>();
                recipe.getIngredientList().forEach(ing -> ingredients.add(new SizedObject<>(ing.getAmount(), ing.toIngredient())));
                recipe.getSecondaryIngredients().forEach(ing -> ingredients.add(new SizedObject<>(ing.getAmount(), ing.toIngredient())));
                FluidStack fluid_input = recipe.getFluidIngredient();

                ArrayList<Object> outputs = new ArrayList<>();
                for (OutputWeightPair output : recipe.getOutputWeightPairList()) {
                    outputs.add(output.getOutput().toItemStack());
                }

                if (recipe.getSecondaryOutputChance() >= 1f) outputs.add(recipe.getSecondaryOutput().toItemStack());
                if (recipe.getTertiaryOutputChance() >= 1f) outputs.add(recipe.getTertiaryOutput().toItemStack());
                if (recipe.getQuaternaryOutputChance() >= 1f) outputs.add(recipe.getQuaternaryOutput().toItemStack());

                addRecipe(outputs, fluid_input, ingredients.toArray());
            }
        }
    }
}
