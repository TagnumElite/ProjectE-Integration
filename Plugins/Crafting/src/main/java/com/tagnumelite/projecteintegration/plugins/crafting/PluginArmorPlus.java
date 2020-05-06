package com.tagnumelite.projecteintegration.plugins.crafting;

import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.base.*;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

@PEIPlugin("armorplus")
public class PluginArmorPlus extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new BenchMapper(BaseCraftingManager.getWBInstance()));
        addMapper(new BenchMapper(BaseCraftingManager.getUTBInstance()));
        addMapper(new BenchMapper(BaseCraftingManager.getHTBInstance()));
        addMapper(new BenchMapper(BaseCraftingManager.getCBInstance()));
    }

    private static class BenchMapper extends PEIMapper {
        private final BaseCraftingManager bench;

        public BenchMapper(BaseCraftingManager bench) {
            super(bench.getName());
            this.bench = bench;
        }

        @Override
        public void setup() {
            for (IRecipe raw : bench.getRecipeList()) {
                ItemStack output = raw.getRecipeOutput();
                if (output == null || output.isEmpty()) continue;

                ArrayList<Object> inputs = new ArrayList<>();
                if (raw instanceof BaseShapedOreRecipe) {
                    inputs.addAll(Arrays.asList(((BaseShapedOreRecipe) raw).getInput()));
                } else if (raw instanceof BaseShapelessOreRecipe) {
                    inputs.addAll(((BaseShapelessOreRecipe) raw).getInput());
                } else if (raw instanceof BaseShapedRecipe) {
                    inputs.addAll(((BaseShapedRecipe) raw).getInput());
                } else if (raw instanceof BaseShapelessRecipe) {
                    inputs.addAll(((BaseShapelessRecipe) raw).getInput());
                } else {
                    continue;
                }

                addRecipe(output, inputs.toArray());
            }
        }
    }
}
