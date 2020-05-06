package com.tagnumelite.projecteintegration.plugins.misc;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import ipsis.Woot;
import ipsis.woot.crafting.IAnvilRecipe;

@PEIPlugin("woot")
public class PluginWoot extends APEIPlugin {
    @Override
    public void setup() throws Exception {
        addMapper(new AnvilMapper());
    }

    private static class AnvilMapper extends PEIMapper {
        public AnvilMapper() {
            super("Anvil");
        }

        @Override
        public void setup() {
            for (IAnvilRecipe recipe : Woot.anvilManager.getRecipes()) {
                if (recipe.shouldPreserveBase()) {
                    addRecipe(recipe.getCopyOutput(), recipe.getInputs().toArray());
                } else {
                    addRecipe(recipe.getCopyOutput(), recipe.getBaseItem(), recipe.getInputs().toArray());
                }
            }
        }
    }
}
