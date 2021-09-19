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
package com.tagnumelite.projecteintegration.plugins.legacy;

import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import ic2.api.classic.recipe.ClassicRecipes;
import ic2.api.classic.recipe.machine.IMachineRecipeList;

import java.util.ArrayList;

@PEIPlugin(value = "ic2", name = "IC2 Classic", config = "ic2classic")
@OnlyIf(versionEndsWith = "!-ex112")
public class PluginIC2Classic extends APEIPlugin {
    @Override
    public void setup() {
        //addMapper(new AdvancedCraftingMapper());
        //addMapper(new CanningMapper());
        addMapper(new MachineMapper("Compressor", ClassicRecipes.compressor));
        addMapper(new ElectrolyzerMapper());
        addMapper(new MachineMapper("Extractor", ClassicRecipes.extractor));
        addMapper(new MachineMapper("Furnace", ClassicRecipes.furnace));
        addMapper(new MachineMapper("Macerator", ClassicRecipes.macerator));
        addMapper(new MachineMapper("Saw Mill", ClassicRecipes.sawMill));
    }

    /*public static class AdvancedCraftingMapper extends PEIMapper {
        public AdvancedCraftingMapper() {
            super("Advanced Crafting");
        }

        @Override
        public void setup() {
            for (IAdvRecipe recipe : ClassicRecipes.advCrafting.getRecipes()) {
                if (!recipe.getRecipeType().isRepair()) {

                }
            }
        }
    }*/

    public static class ElectrolyzerMapper extends PEIMapper {
        public ElectrolyzerMapper() {
            super("Electrolyzer");
        }

        @Override
        public void setup() {
            ClassicRecipes.electrolyzer.getRecipeList().forEach(r -> addRecipe(r.getOutput(), r.getInput()));
        }
    }

    public static class MachineMapper extends PEIMapper {
        private final IMachineRecipeList recipeList;

        public MachineMapper(String name, IMachineRecipeList recipeList) {
            super(name);
            this.recipeList = recipeList;
        }

        @Override
        public void setup() {
            for (IMachineRecipeList.RecipeEntry recipe : recipeList.getRecipeMap()) {
                addRecipe(new ArrayList<>(recipe.getOutput().getAllOutputs()), new SizedObject<>(recipe.getInput().getAmount(), recipe.getInput().getIngredient()));
            }
        }
    }
}
