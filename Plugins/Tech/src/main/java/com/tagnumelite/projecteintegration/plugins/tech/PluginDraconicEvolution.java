package com.tagnumelite.projecteintegration.plugins.tech;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionRecipe;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;

@PEIPlugin("draconicevolution")
public class PluginDraconicEvolution extends APEIPlugin {
    @Override
    public void setup() {
        addEMC(DEFeatures.draconiumDust, 8192);
        // TODO: Figure out why ItemPersitant crashes the plugin when Draconic Evolution isn't installed
        // Raises classNotDefError
        addEMC(DEFeatures.dragonHeart, 262144);
        addEMC(DEFeatures.chaosShard, 1024000);

        addMapper(new FusionMapper());
    }

    private static class FusionMapper extends PEIMapper {
        public FusionMapper() {
            super("fusion", "Enable mapper for Draconic Evolution Fusion Crafting?");
        }

        @Override
        public void setup() {
            for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
                addRecipe(recipe.getRecipeOutput(ItemStack.EMPTY), recipe.getRecipeIngredients().toArray());
            }
        }
    }
}
