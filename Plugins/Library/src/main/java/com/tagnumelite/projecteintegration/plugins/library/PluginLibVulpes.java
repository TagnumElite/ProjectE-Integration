/*
 * Copyright (c) 2019-2020 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tagnumelite.projecteintegration.plugins.library;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.ArrayList;
import java.util.List;

@PEIPlugin("libVulpes")
public class PluginLibVulpes extends APEIPlugin {
    @Override
    public void setup() {
        PEIApi.LOGGER.debug("libVulpes recipe list size: {}", RecipesMachine.getInstance().recipeList.size());
        for (Class<? extends TileMultiblockMachine> clazz : RecipesMachine.getInstance().recipeList.keySet()) {
            PEIApi.LOGGER.debug("Adding new mapper from libvuples {}", clazz.getSimpleName());
            addMapper(new RecipeMapper(clazz));
        }
    }

    @SuppressWarnings("rawtypes")
    protected static class RecipeMapper extends PEIMapper {
        private final Class clazz;

        public RecipeMapper(Class clazz) {
            super(clazz.getSimpleName());
            this.clazz = clazz;
        }

        @Override
        public void setup() {
            for (IRecipe recipe : RecipesMachine.getInstance().getRecipes(clazz)) {
                List<ItemStack> item_outputs = recipe.getOutput();
                List<FluidStack> fluid_outputs = recipe.getFluidOutputs();
                final boolean no_item_out = item_outputs == null || item_outputs.isEmpty();
                final boolean no_fluid_out = fluid_outputs == null || fluid_outputs.isEmpty();
                if (no_item_out && no_fluid_out)
                    continue;

                ArrayList<Object> outputs = new ArrayList<>();

                if (!no_item_out) {
                    outputs.addAll(item_outputs);
                }

                if (!no_fluid_out) {
                    outputs.addAll(fluid_outputs);
                }

                addRecipe(outputs, recipe.getFluidIngredients(), recipe.getIngredients());
            }
        }
    }
}
