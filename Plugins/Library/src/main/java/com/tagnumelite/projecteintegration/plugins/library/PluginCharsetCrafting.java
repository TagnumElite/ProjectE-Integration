package com.tagnumelite.projecteintegration.plugins.library;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import pl.asie.charset.lib.recipe.RecipeCharset;

@PEIPlugin("charset")
public class PluginCharsetCrafting extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new CharsetLibRecipeMapper());
    }

    private static class CharsetLibRecipeMapper extends PEIMapper {
        public CharsetLibRecipeMapper() {
            super("Charset Lib Recipe");
        }

        @Override
        public void setup() {
            for (IRecipe recipe : CraftingManager.REGISTRY) {
                if (recipe instanceof RecipeCharset) {
                    addRecipe(recipe);
                }
            }
        }
    }
}
