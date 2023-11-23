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
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class used for converting {@link Recipe}s to EMC conversion maps.
 * <p>
 * This function has a few helper variables: {@link #recipeID}, {@link #mapper} and {@link #fakeGroupManager}
 * <p>
 * This class follows closely to {@link moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper} with added
 * function for special recipes with {@link ItemStack} and/or {@link FluidStack} outputs.
 * <p>
 * To overwrite output, override method {@link #getOutput(Recipe)}
 * to overwrite input, override method {@link #getIngredients(Recipe)}
 * <p>
 */
//* For an example on how to handle recipes with multiple outputs, look at {@link ImmersiveEngineeringAddon}
public abstract class ARecipeTypeMapper<R extends Recipe<?>> extends ABaseRecipeMapper<R> implements IRecipeTypeMapper {

    @Override
    @SuppressWarnings("unchecked")
    public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Recipe<?> recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
        this.mapper = mapper;
        this.fakeGroupManager = fakeGroupManager;
        this.registryAccess = registryAccess;
        recipeID = recipe.getId();
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
     * @param recipe
     * @return
     */
    public NSSOutput getOutput(R recipe) {
        ItemStack output = recipe.getResultItem(registryAccess);
        if (output.isEmpty()) return null;
        return new NSSOutput(output);
    }

    /**
     * @param recipe
     * @return
     */
    protected List<Ingredient> getIngredients(R recipe) {
        return recipe.getIngredients();
    }

    /**
     * @param recipe
     * @return
     */
    public NSSInput getInput(R recipe) {
        List<Ingredient> ingredients = getIngredients(recipe);
        if (ingredients == null || ingredients.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, ingredients);
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

    /**
     * Returns a list of required mods from the {@link RecipeTypeMapper} annotation
     *
     * @return A list of modids or null.
     */
    public String[] getRequiredMods() {
        RecipeTypeMapper recipeTypeMapperAnnotation = getClass().getAnnotation(RecipeTypeMapper.class);
        if (recipeTypeMapperAnnotation != null) {
            return recipeTypeMapperAnnotation.requiredMods();
        }
        return null;
    }
}
