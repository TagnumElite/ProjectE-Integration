/*
 * Copyright (c) 2019-2022 TagnumElite
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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FTBIndustrialContraptionsAddon {
    public static final String MODID = "ftbic";

    public static String NAME(String name) {
        return "FTBIndustrialContraptions" + name + "Mapper";
    }

    public static abstract class AFTBMachineRecipeMapper extends ARecipeTypeMapper<MachineRecipe> {
        @Override
        public NSSOutput getOutput(MachineRecipe recipe) {
            List<ItemStack> outputItems = recipe.outputItems.stream().filter(s -> s.chance >= 1.0F).map(s -> s.stack).toList();

            List<Object> outputs = Lists.newArrayList();
            outputs.addAll(recipe.outputFluids);
            outputs.addAll(outputItems);

            return mapOutputs(outputs.toArray());
        }

        @Override
        public NSSInput getInput(MachineRecipe recipe) {
            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (IngredientWithCount ingredient : recipe.inputItems) {
                if (!convertIngredient(ingredient.count, ingredient.ingredient, ingredientMap, fakeGroupMap)) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }

            for (FluidStack fluidInput : recipe.inputFluids) {
                if (!convertFluidIngredient(Collections.singletonList(fluidInput), ingredientMap, fakeGroupMap)) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICSmeltingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Smelting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.SMELTING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICMaceratingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Macerating");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.MACERATING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICSeparatingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Separating");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.SEPARATING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICCompressingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Compressing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.COMPRESSING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICReprocessingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Reprocessing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.REPROCESSING.get().recipeType;
        }
    }

    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICCanningMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Canning");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.CANNING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICRollingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Rolling");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.ROLLING.get().recipeType;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class FTBICExtrudingMapper extends AFTBMachineRecipeMapper {
        @Override
        public String getName() {
            return NAME("Extruding");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == FTBICRecipes.EXTRUDING.get().recipeType;
        }
    }
}
