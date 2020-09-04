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
package com.tagnumelite.projecteintegration.plugins.tech;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import forestry.api.recipes.*;
import net.minecraftforge.fluids.FluidStack;

@PEIPlugin("forestry")
public class PluginForestry extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new CarpenterMapper());
        //addMapper(new CentrifugeMapper()); //INFO: Has multiple outputs, not doing for now
        addMapper(new FabricatorMapper());
        addMapper(new FermenterMapper());
        addMapper(new MoistenerMapper());
        addMapper(new SqueezerMapper());
        addMapper(new StillMapper());
    }

    private static class CarpenterMapper extends PEIMapper {
        public CarpenterMapper() {
            super("Carpenter");
        }

        @Override
        public void setup() {
            for (ICarpenterRecipe recipe : RecipeManagers.carpenterManager.recipes()) {
                addRecipe(recipe.getBox(), recipe.getCraftingGridRecipe().getRawIngredients().toArray(), recipe.getFluidResource());
            }
        }
    }

    private static class FabricatorMapper extends PEIMapper {
        public FabricatorMapper() {
            super("Fabricator");
        }

        @Override
        public void setup() {
            RecipeManagers.fabricatorManager.recipes().forEach(r -> addRecipe(r.getRecipeOutput(), r.getLiquid(), Utils.convertGrid(r.getIngredients())));
        }
    }

    private static class FermenterMapper extends PEIMapper {
        public FermenterMapper() {
            super("Fermenter");
        }

        @Override
        public void setup() {
            RecipeManagers.fermenterManager.recipes().forEach(r -> addRecipe(
                new FluidStack(r.getOutput(), (int) (r.getFermentationValue() * r.getModifier())),
                r.getFluidResource(), r.getResource(), r.getResourceOreName()
            ));
        }
    }

    private static class MoistenerMapper extends PEIMapper {
        public MoistenerMapper() {
            super("Moistener");
        }

        @Override
        public void setup() {
            RecipeManagers.moistenerManager.recipes().forEach(r -> addRecipe(r.getProduct(), r.getResource()));
        }
    }

    private static class SqueezerMapper extends PEIMapper {
        public SqueezerMapper() {
            super("Squeezer");
        }

        @Override
        public void setup() {
            for (ISqueezerRecipe recipe : RecipeManagers.squeezerManager.recipes()) {
                addRecipe(recipe.getFluidOutput(), recipe.getResources());
            }
        }
    }

    private static class StillMapper extends PEIMapper {
        public StillMapper() {
            super("Still");
        }

        @Override
        public void setup() {
            RecipeManagers.stillManager.recipes().forEach(r -> addRecipe(r.getOutput(), r.getInput()));
        }
    }
}
