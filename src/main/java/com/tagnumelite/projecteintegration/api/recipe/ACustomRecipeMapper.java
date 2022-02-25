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
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class ACustomRecipeMapper<R> extends ABaseRecipeMapper<R> {
    public abstract List<R> getRecipes();

    protected abstract List<Ingredient> getIngredients(R recipe);

    protected abstract ItemStack getResult(R recipe);

    @Override
    public NSSInput getInput(R recipe) {
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
    public NSSOutput getOutput(R recipe) {
        ItemStack output = getResult(recipe);
        if (output.isEmpty()) return null;
        return new NSSOutput(output);
    }

    /**
     * @param mapper
     * @param recipe
     * @param fakeGroupManager
     * @return
     */
    @SuppressWarnings("unchecked")
    public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Object recipe, INSSFakeGroupManager fakeGroupManager) {
        this.recipeID = new ResourceLocation(getRequiredMods()[0], getName().toLowerCase());
        this.mapper = mapper;
        this.fakeGroupManager = fakeGroupManager;
        try {
            return convertRecipe((R) recipe);
        } catch (ClassCastException e) {
            PEIntegration.LOGGER.fatal("RecipeMapper ({}) is unable to handle recipe ({}), expected ({})",
                    getClass().getName(), recipe.getClass().getName(),
                    ((Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName());
        } catch (Exception e) {
            PEIntegration.LOGGER.fatal("RecipeMapper ({}) failed unexpectedly during the handling of recipe '{}' ({}).",
                    getClass().getName(), recipeID, recipe.getClass().getName(), e);
        }
        return false;
    }

    /**
     * Returns an array of one required mod from the {@link CustomRecipeMapper} annotation
     *
     * @return An array of a modid or a single array of 'unregistered_mapper'.
     */
    public String[] getRequiredMods() {
        CustomRecipeMapper recipeTypeMapperAnnotation = getClass().getAnnotation(CustomRecipeMapper.class);
        if (recipeTypeMapperAnnotation != null) {
            return new String[]{recipeTypeMapperAnnotation.value()};
        }
        return new String[]{"unregistered_mapper"};
    }
}
