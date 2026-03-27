/*
 * Copyright (c) 2019-2026 TagnumElite
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
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import wootrevived.woot.recipes.fluid_infuser.FluidInfuserRecipe;
import wootrevived.woot.recipes.item_infuser.ItemInfuserRecipe;
import wootrevived.woot.recipes.stygian_anvil.StygianAnvilRecipe;
import wootrevived.woot.registries.RecipesRegistry;

import java.util.ArrayList;
import java.util.List;

// FactoryRecipe and EnchantSqueezerRecipe not supported. You can guess why.
public class WootRevivedAddon {
    public static final String MODID = "woot_revived";
    public static final NormalizedSimpleStack WHITE_STACK = NSSFake.create("woot_white_dye_stack");
    public static final NormalizedSimpleStack RED_STACK = NSSFake.create("woot_red_dye_stack");
    public static final NormalizedSimpleStack YELLOW_STACK = NSSFake.create("woot_yellow_dye_stack");
    public static final NormalizedSimpleStack BLUE_STACK = NSSFake.create("woot_blue_dye_stack");

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootAnvilMapper extends ARecipeTypeMapper<StygianAnvilRecipe> {
        @Override
        public String getName( ) {
            return "WootAnvilMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipesRegistry.ANVIL_RECIPE_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(StygianAnvilRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        public List<Ingredient> getIngredients(StygianAnvilRecipe recipe) {
            ArrayList<Ingredient> ingredients = new ArrayList<>();

            ingredients.add(recipe.getBase());
            recipe.getFirstComplementary().ifPresent(ingredients::add);
            recipe.getSecondComplementary().ifPresent(ingredients::add);
            recipe.getThirdComplementary().ifPresent(ingredients::add);
            recipe.getFourthComplementary().ifPresent(ingredients::add);

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
        public boolean canHandle(RecipeType<?>recipeType) {
            returnrecipeType == DyeSqueezerRecipe.DYE_SQUEEZER_TYPE;
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
    public static class WootFluidConvertorMapper extends ARecipeTypeMapper<FluidInfuserRecipe> {
        @Override
        public String getName( ) {
            return "WootFluidConvertorMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipesRegistry.FLUID_INFUSER_RECIPE_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(FluidInfuserRecipe recipe) {
            return new NSSOutput(recipe.getOutputFluid());
        }

        @Override
        public NSSInput getInput(FluidInfuserRecipe recipe) {
            return getInputBuilder().addIngredient(recipe.getIngredient()).addFluid(recipe.getInputFluid()).build();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class WootInfuserMapper extends ARecipeTypeMapper<ItemInfuserRecipe> {
        @Override
        public String getName( ) {
            return "WootInfuserMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipesRegistry.ITEM_INFUSER_RECIPE_TYPE.get();
        }

        @Override
        public NSSInput getInput(ItemInfuserRecipe recipe) {
            NSSInput.Builder builder = getInputBuilder();

            builder.addIngredient(recipe.getIngredient());
            recipe.getAugment().ifPresent(builder::addIngredient);

            if (!recipe.getFluid().isEmpty()) {
                builder.addFluid(recipe.getFluid());
            }

            return builder.build();
        }

        @Override
        public NSSOutput getOutput(ItemInfuserRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }
}
