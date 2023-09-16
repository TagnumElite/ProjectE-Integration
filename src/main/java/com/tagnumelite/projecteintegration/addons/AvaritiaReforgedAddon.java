/*
 * Copyright (c) 2019-2023 TagnumElite
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
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import committee.nova.mods.avaritia.common.crafting.recipe.ICompressorRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ICraftRecipe;
import committee.nova.mods.avaritia.init.registry.ModItems;
import committee.nova.mods.avaritia.init.registry.ModRecipeTypes;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class AvaritiaReforgedAddon {
    public static final String MODID = "avaritia";

    protected static String NAME(String name) {
        return "AvaritiaReforged" + name + "Mapper";
    }


    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ARCrafterMapper extends ARecipeTypeMapper<ICraftRecipe> {

        @Override
        public String getName() {
            return NAME("ExtremeCrafter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.EXTREME_CRAFT_RECIPE.get();
        }
    }


    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ARCompressorMapper extends ARecipeTypeMapper<ICompressorRecipe> {

        @Override
        public String getName() {
            return NAME("Compressor");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipeTypes.COMPRESSOR_RECIPE.get();
        }

        @Override
        public NSSInput getInput(ICompressorRecipe recipe) {
            List<Ingredient> ingredients = getIngredients(recipe);
            if (ingredients == null || ingredients.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, ingredients);
                return null;
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            Ingredient ingredient = ingredients.get(0);
            if (!convertIngredient(recipe.getInputCount(), ingredient, ingredientMap, fakeGroupMap)) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @ConversionProvider(MODID)
    public static class ARConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Avaritia: Reforged.")
                    .before(ModItems.neutron_pile.get(), 128);
        }
    }
}
