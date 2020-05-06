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
package com.tagnumelite.projecteintegration.plugins.magic;

import WayofTime.bloodmagic.api.impl.BloodMagicAPI;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyArray;
import WayofTime.bloodmagic.api.impl.recipe.RecipeAlchemyTable;
import WayofTime.bloodmagic.api.impl.recipe.RecipeBloodAltar;
import WayofTime.bloodmagic.api.impl.recipe.RecipeTartaricForge;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;

@PEIPlugin("bloodmagic")
public class PluginBloodMagic extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new AlchemyArrayMapper());
        addMapper(new AlchemyTableMapper());
        addMapper(new BloodAltarMapper());
        addMapper(new TartaricForgeMapper());
    }

    private static class AlchemyArrayMapper extends PEIMapper {
        public AlchemyArrayMapper() {
            super("Alchemy Array");
        }

        @Override
        public void setup() {
            for (RecipeAlchemyArray recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyArrayRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getCatalyst(), recipe.getInput());
            }
        }
    }

    private static class AlchemyTableMapper extends PEIMapper {
        public AlchemyTableMapper() {
            super("Alchemy Table");
        }

        @Override
        public void setup() {
            for (RecipeAlchemyTable recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAlchemyRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput().toArray()); //TODO: Add EMC to ... something?
            }
        }
    }

    private static class BloodAltarMapper extends PEIMapper {
        public BloodAltarMapper() {
            super("Blood Altar");
        }

        @Override
        public void setup() {
            for (RecipeBloodAltar recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getAltarRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput()); //TODO: Add EMC to blood
            }
        }
    }

    private static class TartaricForgeMapper extends PEIMapper {
        public TartaricForgeMapper() {
            super("Tartaric Forge");
        }

        @Override
        public void setup() {
            for (RecipeTartaricForge recipe : BloodMagicAPI.INSTANCE.getRecipeRegistrar().getTartaricForgeRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput().toArray()); //TODO: Add EMC to Souls
            }
        }
    }
}
