/*
 * Copyright (c) 2019-2021 TagnumElite
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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import novamachina.exnihilosequentia.api.crafting.ItemStackWithChance;
import novamachina.exnihilosequentia.api.crafting.crucible.CrucibleRecipe;
import novamachina.exnihilosequentia.api.crafting.fluiditem.FluidItemRecipe;
import novamachina.exnihilosequentia.api.crafting.fluidontop.FluidOnTopRecipe;
import novamachina.exnihilosequentia.api.crafting.fluidtransform.FluidTransformRecipe;
import novamachina.exnihilosequentia.api.crafting.hammer.HammerRecipe;
import novamachina.exnihilosequentia.api.crafting.sieve.SieveRecipe;

import java.util.Collections;
import java.util.List;

public class ExNihiloSequentiaAddon {
    public static final String MODID = "exnihilosequentia";

    static String NAME(String name) {
        return "ExNihiloSequentia" + name + "Mapper";
    }

    /* Leave compost and crooks disabled, makes no sense to have them here
        @RecipeTypeMapper(requiredMods = MODID, priority = 1)
        public static class ENSCompostMapper extends APEIRecipeMapper<CompostRecipe> {
            @Override
            public String getName() {
                return NAME("Compost");
            }

            @Override
            public String getDescription() {
                return "";
            }

            @Override
            public boolean canHandle(IRecipeType<?> iRecipeType) {
                return iRecipeType == CompostRecipe.RECIPE_TYPE;
            }

            @Override
            protected NSSInput getInput(CompostRecipe recipe) {
                IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
                List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
                convertIngredient(recipe.getAmount(), recipe.getInput(), ingredientMap, fakeGroupMap);
                return new NSSInput(ingredientMap, fakeGroupMap, true);
            }

            @Override
            protected NSSOutput getOutput(CompostRecipe recipe) {
                return new NSSOutput(new ItemStack(Items.DIRT, 1));
            }
        }
        @RecipeTypeMapper(requiredMods = MODID, priority = 1)
        public static class ENSCrookMapper extends APEIRecipeMapper<CrookRecipe> {

            @Override
            public String getName() {
                return NAME("Crook");
            }

            @Override
            public String getDescription() {
                return "null";
            }

            @Override
            public boolean canHandle(IRecipeType<?> iRecipeType) {
                return iRecipeType ==CrookRecipe.RECIPE_TYPE;
            }
        }
     */
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSCrucibleMapper extends ARecipeTypeMapper<CrucibleRecipe> {
        @Override
        public String getName() {
            return NAME("Crucible");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == CrucibleRecipe.RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(CrucibleRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(CrucibleRecipe recipe) {
            return new NSSOutput(recipe.getAmount(), NSSFluid.createFluid(recipe.getResultFluid()));
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSFluidItemTransformationMapper extends ARecipeTypeMapper<FluidItemRecipe> {
        @Override
        public String getName() {
            return NAME("FluidItemTransformation");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == FluidItemRecipe.RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(FluidItemRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSFluidOnTopMapper extends ARecipeTypeMapper<FluidOnTopRecipe> {
        @Override
        public String getName() {
            return NAME("FluidOnTop");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == FluidOnTopRecipe.RECIPE_TYPE;
        }

        @Override
        public NSSInput getInput(FluidOnTopRecipe recipe) {
            return NSSInput.createFluid(recipe.getFluidInTank());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSFluidTransformationMapper extends ARecipeTypeMapper<FluidTransformRecipe> {
        @Override
        public String getName() {
            return NAME("FluidTransformation");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == FluidTransformRecipe.RECIPE_TYPE;
        }

        @Override
        public NSSInput getInput(FluidTransformRecipe recipe) {
            return NSSInput.createFluid(recipe.getFluidInTank());
        }

        @Override
        public NSSOutput getOutput(FluidTransformRecipe recipe) {
            return new NSSOutput(recipe.getResult());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSHammerMapper extends ARecipeTypeMapper<HammerRecipe> {
        @Override
        public String getName() {
            return NAME("Hammer");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == HammerRecipe.RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(HammerRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(HammerRecipe recipe) {
            Object[] outputs = recipe.getOutput().stream().filter(output -> output.getChance() >= 1f).map(ItemStackWithChance::getStack).toArray();

            if (outputs.length == 0) {
                PEIntegration.debugLog("Recipe ({}) contains no guaranteed outputs", recipeID);
                return NSSOutput.EMPTY;
            }

            return mapOutputs(outputs);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ENSSieveMapper extends ARecipeTypeMapper<SieveRecipe> {
        @Override
        public String getName() {
            return NAME("Sieve");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Only maps guaranteed drop, ignore chanced items.";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == SieveRecipe.RECIPE_TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(SieveRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(SieveRecipe recipe) {
            return new NSSOutput(recipe.getDrop());
        }
    }
}
