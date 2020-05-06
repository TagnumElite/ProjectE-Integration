package com.tagnumelite.projecteintegration.plugins.magic;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.recipe.TrickRecipe;

@PEIPlugin("psi")
public class PluginPsi extends APEIPlugin {
    @Override
    public void setup() throws Exception {
        addMapper(new TrickMapper());
    }

    private static class TrickMapper extends PEIMapper {
        public TrickMapper() {
            super("Trick");
        }

        @Override
        public void setup() {
            for (TrickRecipe recipe : PsiAPI.trickRecipes) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }
}
