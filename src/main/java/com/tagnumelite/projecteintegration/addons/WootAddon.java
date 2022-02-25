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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import ipsis.woot.crafting.AnvilRecipe;
import ipsis.woot.crafting.FluidConvertorRecipe;
import ipsis.woot.crafting.InfuserRecipe;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

// FactoryRecipe and EnchantSqueezerRecipe not supported. You can guess why.
public class WootAddon {
    public static final String MODID = "woot";
    public static final NormalizedSimpleStack WHITE_STACK = NSSFake.create("woot_white_dye_stack");
    public static final NormalizedSimpleStack RED_STACK = NSSFake.create("woot_red_dye_stack");
    public static final NormalizedSimpleStack YELLOW_STACK = NSSFake.create("woot_yellow_dye_stack");
    public static final NormalizedSimpleStack BLUE_STACK = NSSFake.create("woot_blue_dye_stack");

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootAnvilMapper extends ARecipeTypeMapper<AnvilRecipe> {
        @Override
        public String getName() {
            return "WootAnvilMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AnvilRecipe.ANVIL_TYPE;
        }

        @Override
        public NSSOutput getOutput(AnvilRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        public List<Ingredient> getIngredients(AnvilRecipe recipe) {
            ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
            ingredients.add(recipe.getBaseIngredient());
            return ingredients;
        }
    }

    /* TODO: I'll deal with this later
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootDyeSqueezerMapper extends APEIRecipeMapper<DyeSqueezerRecipe> {
        @Override
        public String getName() {
            return "WootDyeSqueezerMapper";
        }

        @Override
        public String getDescription() {
            return "Recipe mapper for Woot Dye Squeezer";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == DyeSqueezerRecipe.DYE_SQUEEZER_TYPE;
        }

        @Override
        protected boolean convertRecipe(DyeSqueezerRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> outputMap = new IngredientMap<>();

            // TODO: There is potential for optimization here.
            Set<NormalizedSimpleStack> outputStacks = new HashSet<>(4);
            int total = 0;
            final int red = recipe.getRed();
            if (red > 0) {
                outputMap.addIngredient(RED_STACK, red);
                total += red;
                outputStacks.add(RED_STACK);
            }

            final int yellow = recipe.getYellow();
            if (yellow > 0) {
                outputMap.addIngredient(YELLOW_STACK, yellow);
                total += yellow;
                outputStacks.add(YELLOW_STACK);
            }

            final int blue = recipe.getBlue();
            if (blue > 0) {
                outputMap.addIngredient(BLUE_STACK, blue);
                total += blue;
                outputStacks.add(BLUE_STACK);
            }

            final int white = recipe.getWhite();
            if (white > 0) {
                outputMap.addIngredient(WHITE_STACK, white);
                total += white;
                outputStacks.add(WHITE_STACK);
            }

            NormalizedSimpleStack dummy = fakeGroupManager.getOrCreateFakeGroup(outputStacks).getA();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            if (!convertIngredient(total, recipe.getIngredient(), ingredientMap, fakeGroupMap)) {
                return addConversionsAndReturn(fakeGroupMap, false);
            }

            Map<NormalizedSimpleStack, Integer> mappedOutput = outputMap.getMap();

            if (red > 0) {
                mapper.addConversion(red, RED_STACK, red);
            }

            if (yellow > 0) {
                mapper.addConversion(yellow, YELLOW_STACK, yellow);
            }

            if (blue > 0) {
                mapper.addConversion(blue, BLUE_STACK, blue);
            }

            if (white > 0) {
                mapper.addConversion(white, WHITE_STACK, white);
            }

            return addConversionsAndReturn(fakeGroupMap, true);
        }
    }
    */
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootFluidConvertorMapper extends ARecipeTypeMapper<FluidConvertorRecipe> {
        @Override
        public String getName() {
            return "WootFluidConvertorMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == FluidConvertorRecipe.FLUID_CONV_TYPE;
        }

        @Override
        public NSSOutput getOutput(FluidConvertorRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        public NSSInput getInput(FluidConvertorRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            if (!convertIngredient(recipe.getCatalyst(), ingredientMap, fakeGroupMap)) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }
            ingredientMap.addIngredient(NSSFluid.createFluid(recipe.getInputFluid()), recipe.getInputFluid().getAmount());
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootInfuserMapper extends ARecipeTypeMapper<InfuserRecipe> {
        @Override
        public String getName() {
            return "WootInfuserMapper";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == InfuserRecipe.INFUSER_TYPE;
        }

        @Override
        public NSSInput getInput(InfuserRecipe recipe) {
            List<Ingredient> ingredients = new ArrayList<>(1);
            ingredients.add(recipe.getIngredient());
            if (recipe.getAugment() != null) {
                ingredients.add(recipe.getAugment());
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            if (recipe.getFluidInput() != null) {
                FluidStack fluid = recipe.getFluidInput();
                ingredientMap.addIngredient(NSSFluid.createFluid(fluid), fluid.getAmount());
            }

            for (Ingredient ingredient : ingredients) {
                if (!convertIngredient(ingredient, ingredientMap, fakeGroupMap)) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        public NSSOutput getOutput(InfuserRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }
}
