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
import com.tagnumelite.projecteintegration.addons.ImmersiveEngineeringAddon;
import com.tagnumelite.projecteintegration.api.Utils;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * An abstract class used for converting {@link IRecipe}s to EMC conversion maps.
 * <p>
 * This function has a few helper variables: {@link #recipeID}, {@link #mapper} and {@link #fakeGroupManager}
 * <p>
 * This class follows closely to {@link moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper} with added
 * function for special recipes with {@link ItemStack} and/or {@link FluidStack} outputs.
 * <p>
 * To overwrite output, override method {@link #getOutput(IRecipe)}
 * to overwrite input, override method {@link #getIngredients(IRecipe)}
 * <p>
 * For an example on how to handle recipes with multiple outputs, look at {@link ImmersiveEngineeringAddon}
 */
public abstract class ARecipeTypeMapper<R extends IRecipe<?>> implements IRecipeMapper<R>, IRecipeTypeMapper {
    protected ResourceLocation recipeID;
    protected IMappingCollector<NormalizedSimpleStack, Long> mapper;
    protected INSSFakeGroupManager fakeGroupManager;

    /**
     * Convert the recipe into an EMC map
     *
     * @param recipe The recipe to be converted into an EMC Map
     * @return A boolean value denoting whether the recipe was handled successfully or not.
     */
    public boolean convertRecipe(R recipe) {
        NSSOutput output = getOutput(recipe);
        if (output == null || output.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no outputs: {}", recipeID, recipe.getResultItem());
            return false;
        }

        NSSInput input = getInput(recipe);
        if (input == null || !input.successful) {
            return addConversionsAndReturn(input != null ? input.fakeGroupMap : null, true);
        }

        mapper.addConversion(output.amount, output.nss, input.getMap());
        return addConversionsAndReturn(input.fakeGroupMap, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> iRecipe, INSSFakeGroupManager fakeGroupManager) {
        this.mapper = mapper;
        this.fakeGroupManager = fakeGroupManager;
        recipeID = iRecipe.getId();
        try {
            return convertRecipe((R) iRecipe);
        } catch (ClassCastException e) {
            PEIntegration.LOGGER.fatal("RecipeMapper ({}) is unable to handle recipe ({}), expected ({})",
                    getClass().getName(), iRecipe.getClass().getName(),
                    ((Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName());
        } catch (Exception e) {
            PEIntegration.LOGGER.fatal("RecipeMapper ({}) failed unexpectedly during the handling of recipe '{}' ({}).",
                    getClass().getName(), recipeID, iRecipe.getClass().getName(), e);
        }
        return false;
    }

    /**
     * @param recipe
     * @return
     */
    public NSSOutput getOutput(R recipe) {
        ItemStack output = recipe.getResultItem();
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
     *
     * @param fluidIngredients
     * @return
     */
    protected NSSInput convertFluidIngredients(List<List<FluidStack>> fluidIngredients) {
        if (fluidIngredients == null || fluidIngredients.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, fluidIngredients);
            return null;
        }

        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

        for (List<FluidStack> ingredient : fluidIngredients) {
            if (!convertFluidIngredient(ingredient, ingredientMap, fakeGroupMap)) {
                return new NSSInput(ingredientMap, fakeGroupMap, false);
            }
        }
        return new NSSInput(ingredientMap, fakeGroupMap, true);
    }

    /**
     *
     * @param fluidIngredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertFluidIngredient(List<FluidStack> fluidIngredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                             List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        return convertFluidIngredient(-1, fluidIngredient, ingredientMap, fakeGroupMap);
    }

    /**
     *
     * @param amount
     * @param fluidIngredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertFluidIngredient(int amount, List<FluidStack> fluidIngredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        return Utils.convertFluidIngredient(amount, fluidIngredient,ingredientMap,fakeGroupMap,fakeGroupManager,recipeID.toString());
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
        return Utils.convertIngredient(amount, ingredient, ingredientMap, fakeGroupMap, fakeGroupManager, recipeID.toString());
    }

    /**
     * @param allOutputs
     * @return
     */
    protected NSSOutput mapOutputs(Object... allOutputs) {
        return Utils.mapOutputs(mapper, fakeGroupManager, recipeID.toString(), allOutputs);
    }

    protected NSSOutput mapOutput(Object... outputVariants) {
        return Utils.mapOutput(mapper, fakeGroupManager, recipeID.toString(), outputVariants);
    }

    /**
     * @param ingredientMap
     * @param stack
     * @return
     */
    protected boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, FluidStack stack) {
        return Utils.addIngredient(ingredientMap, stack);
    }

    //TODO: CHANGE THE BELOW CODE SOON!

    /**
     * This method can be used as a helper method to return a specific value and add any existing group conversions. It is important that we add any valid group
     * conversions that we have, regardless of whether the recipe as a whole is valid, because we only create one instance of our group's NSS representation so even if
     * parts of the recipe are not valid, the conversion may be valid and exist in another recipe.
     */
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
     * @param stacks
     * @return
     */
    protected Tuple<NormalizedSimpleStack, Boolean> getFakeGroup(NormalizedSimpleStack... stacks) {
        return fakeGroupManager.getOrCreateFakeGroup(new HashSet<>(Arrays.asList(stacks)));
    }
}
