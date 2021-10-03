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

package com.tagnumelite.projecteintegration.api.recipe;

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.Utils;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class ACustomRecipeMapper implements IRecipeMapper<Object> {
    private IMappingCollector<NormalizedSimpleStack, Long> mapper;
    private INSSFakeGroupManager fakeGroupManager;

    public abstract List<Object> getRecipes();

    protected abstract List<Ingredient> getIngredients(Object recipe);

    protected abstract ItemStack getResult(Object recipe);

    @Override
    public NSSInput getInput(Object recipe) {
        List<Ingredient> ingredients = getIngredients(recipe);
        if (ingredients == null || ingredients.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipe, ingredients);
            return null;
        }

        // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

        for (Ingredient ingredient : ingredients) {
            if (!convertIngredient(ingredient, ingredientMap, fakeGroupMap)) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }
        }
        return new NSSInput(ingredientMap, fakeGroupMap, true);
    }

    @Override
    public NSSOutput getOutput(Object recipe) {
        ItemStack output = getResult(recipe);
        if (output.isEmpty()) return null;
        return new NSSOutput(output);
    }

    @Override
    public boolean convertRecipe(Object recipe) {
        NSSOutput output = getOutput(recipe);
        if (output == null || output.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no outputs: {}", recipe, output);
            return false;
        }

        NSSInput input = getInput(recipe);
        if (input == null || !input.successful) {
            return addConversionsAndReturn(input != null ? input.fakeGroupMap : null, true);
        }

        mapper.addConversion(output.amount, output.nss, input.getMap());
        return addConversionsAndReturn(input.fakeGroupMap, true);
    }

    /**
     * @param ingredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertIngredient(Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        return convertIngredient(-1, ingredient, ingredientMap, fakeGroupMap);
    }

    /**
     * @param amount
     * @param ingredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertIngredient(int amount, Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        return Utils.convertIngredient(amount, ingredient, ingredientMap, fakeGroupMap, fakeGroupManager, null);
    }

    protected boolean addConversionsAndReturn(List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos, boolean returnValue) {
        //If we have any conversions make sure to add them even if we are returning early
        if (dummyGroupInfos != null) {
            for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
                for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
                    mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
                }
            }
        }
        return returnValue;
    }

    /**
     * @param mapper
     * @param recipe
     * @param fakeGroupManager
     * @return
     */
    public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Object recipe, INSSFakeGroupManager fakeGroupManager) {
        this.mapper = mapper;
        this.fakeGroupManager = fakeGroupManager;
        return convertRecipe(recipe);
    }
}
