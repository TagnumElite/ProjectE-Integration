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

package com.tagnumelite.projecteintegration.api.utils;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.lists.InputList;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedIngredient;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IngredientHandler {
    private final IngredientMap<Object> ingredients;

    public IngredientHandler() {
        this.ingredients = new IngredientMap<>();
    }

    /**
     * @param ingredients An array of objects to be added to the {@link IngredientMap}
     * @return {@code true} if all ingredients were added or {@code false} if one or all ingredients failed to be added.
     */
    public boolean addAll(Object[] ingredients) {
        boolean result = true;
        for (Object ingredient : ingredients) {
            if (!add(ingredient)) result = false;
        }
        return result;
    }

    /**
     * Note, all arrays and lists will by assumed to be a list of ingredients, if it is a list of possibilities for an
     * single ingredient, then use {@link InputList} to denote that.
     *
     * @param ingredient The object to be added to the {@link IngredientMap}
     * @return {@code true} if the ingredient was successfully added or {@code false} if it failed
     */
    public boolean add(Object ingredient) {
        if (ingredient == null) return false;

        if (ingredient instanceof ItemStack) {
            if (((ItemStack) ingredient).isEmpty()) return false;
            ingredients.addIngredient(ingredient, ((ItemStack) ingredient).getCount());
        } else if (ingredient instanceof Item || ingredient instanceof Block || ingredient instanceof String
            || ingredient.getClass().equals(Object.class)) {
            ingredients.addIngredient(ingredient, 1);
        } else if (ingredient instanceof FluidStack) {
            if (((FluidStack) ingredient).amount <= 0) return false;

            ingredients.addIngredient(ingredient, ((FluidStack) ingredient).amount);
        } else if (ingredient instanceof List) {
            List<?> inputt = (List<?>) ingredient;
            if (inputt.isEmpty()) return false;
            if (inputt.size() == 1) return add(inputt.get(0));

            if (inputt instanceof InputList) {
                ingredients.addIngredient(PEIApi.getList(inputt), 1);
            } else {
                return addAll(inputt.toArray());
            }
        } else if (ingredient instanceof Ingredient) {
            if (ingredient == Ingredient.EMPTY) return false;
            ingredients.addIngredient(PEIApi.getIngredient((Ingredient) ingredient), 1);
        } else if (ingredient instanceof SizedIngredient) {
            SizedIngredient inp = (SizedIngredient) ingredient;
            if (inp.object == Ingredient.EMPTY)
                return false;

            ingredients.addIngredient(PEIApi.getIngredient(inp.object), inp.amount);
        } else if (ingredient instanceof SizedObject) {
            SizedObject<?> sized = (SizedObject<?>) ingredient;
            if (sized.object == null)
                return false;

            ingredients.addIngredient(sized.object, sized.amount);
        } else if (ingredient instanceof Object[]) {
            InputList<Object> inputList = new InputList<>();
            inputList.addAll(Arrays.asList((Object[]) ingredient));
            return add(inputList);
        } else {
            PEIApi.LOG.warn("Unknown ingredient: {} ({})", ingredient, ClassUtils.getPackageCanonicalName(ingredient.getClass()));
            return false;
        }
        return true;
    }

    public Map<Object, Integer> getMap() {
        return ingredients.getMap();
    }
}
