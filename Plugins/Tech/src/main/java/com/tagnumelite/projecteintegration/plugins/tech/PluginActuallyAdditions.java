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
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import de.ellpeck.actuallyadditions.api.recipe.EmpowererRecipe;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import de.ellpeck.actuallyadditions.mod.items.InitItems;

import java.util.ArrayList;

@PEIPlugin("actuallyadditions")
public class PluginActuallyAdditions extends APEIPlugin {
    @Override
    public void setup() {
        addEMC(InitItems.itemMisc, 13, 64);
        addEMC(InitItems.itemCoffeeBean, 64);
        addEMC(InitItems.itemFoods, 16, 64);
        addEMC(InitItems.itemMisc, 15, 480);
        addEMC(InitItems.itemSolidifiedExperience, 863);

        addMapper(new EmpowererMapper());
        addMapper(new ReconstructorMapper());
        addMapper(new CrusherMapper());
    }

    private static class EmpowererMapper extends PEIMapper {
        public EmpowererMapper() {
            super("Empowerer");
        }

        @Override
        public void setup() {
            for (EmpowererRecipe recipe : ActuallyAdditionsAPI.EMPOWERER_RECIPES) {
                addRecipe(recipe.getOutput(), recipe.getInput(), recipe.getStandOne(), recipe.getStandTwo(),
                    recipe.getStandThree(), recipe.getStandFour());
            }
        }
    }

    private static class ReconstructorMapper extends PEIMapper {
        public ReconstructorMapper() {
            super("Reconstructor");
        }

        @Override
        public void setup() {
            for (LensConversionRecipe recipe : ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }

    private static class CrusherMapper extends PEIMapper {
        public CrusherMapper() {
            super("crusher");
        }

        @Override
        public void setup() {
            for (CrusherRecipe recipe : ActuallyAdditionsAPI.CRUSHER_RECIPES) {
                if (recipe.getSecondChance() >= 100) {
                    ArrayList<Object> output = new ArrayList<>();
                    output.add(recipe.getOutputOne());
                    output.add(recipe.getOutputTwo());
                    addRecipe(output, recipe.getInput());
                } else {
                    addRecipe(recipe.getOutputOne(), recipe.getInput());
                }
            }
        }
    }
}
