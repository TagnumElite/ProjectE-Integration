package com.tagnumelite.projecteintegration.plugins.magic;

import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.common.crafting.recipe.RecipePedestal;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;

@PEIPlugin("extrabotany")
public class PluginExtraBotany extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new PedestalMapper());
    }

    private static class PedestalMapper extends PEIMapper {
        public PedestalMapper() {
            super("Pedestal");
        }

        @Override
        public void setup() {
            for (RecipePedestal recipe : ExtraBotanyAPI.pedestalRecipes) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }
}
