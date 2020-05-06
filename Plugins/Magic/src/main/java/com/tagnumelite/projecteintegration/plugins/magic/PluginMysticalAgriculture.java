package com.tagnumelite.projecteintegration.plugins.magic;

import com.blakebr0.mysticalagriculture.crafting.ReprocessorManager;
import com.blakebr0.mysticalagriculture.crafting.ReprocessorRecipe;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;

@PEIPlugin("mysticalagriculture")
public class PluginMysticalAgriculture extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new SeedProcessorMapper());
    }

    private static class SeedProcessorMapper extends PEIMapper {
        public SeedProcessorMapper() {
            super("Seed Processor");
        }

        @Override
        public void setup() {
            for (ReprocessorRecipe recipe : ReprocessorManager.getRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput()); //TODO: Improve, to support ore dict and other
            }
        }
    }
}
