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
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        if (Objects.isNull(ingredient)) return false;

        SizedObject<Object> object;
        if (ingredient instanceof SizedObject) {
            SizedObject<Object> converted = convert(((SizedObject<?>) ingredient).object);
            if (Objects.isNull(converted)) return false;
            object = new SizedObject<>(((SizedObject<?>) ingredient).amount, converted.object);
        } else {
            object = convert(ingredient);
        }
        if (Objects.isNull(object)) return false;
        ingredients.addIngredient(object.object, object.amount);
        return true;
    }

    public SizedObject<Object> convert(Object obj) {
        if (Objects.isNull(obj)) return null;
        int amount;
        if (obj instanceof ItemStack) {
            if (((ItemStack) obj).isEmpty()) {
                return null;
            }

            amount = ((ItemStack) obj).getCount();
        } else if (obj instanceof Item || obj instanceof Block || obj instanceof String || obj.getClass().equals(Object.class)) {
            amount = 1;
        } else if (obj instanceof FluidStack) {
            PEIApi.LOG.info("FluidStack");
            if (((FluidStack) obj).amount <= 0) {
                return null;
            }

            amount = ((FluidStack) obj).amount;
        } else if (obj instanceof List) {
            List<?> inputt = (List<?>) obj;
            if (inputt.isEmpty()) {
                return null;
            }
            if (inputt.size() == 1) {
                return convert(inputt.get(0));
            }

            amount = 1;
            if (inputt instanceof InputList) {
                obj = PEIApi.getList(inputt);
            } else {
                addAll(inputt.toArray());
                return null;
            }
        } else if (obj instanceof Ingredient) {
            if (obj == Ingredient.EMPTY) {
                return null;
            }
            amount = 1;
            obj = PEIApi.getIngredient((Ingredient) obj);
        } else if (obj instanceof Object[]) {
            addAll((Object[]) obj);
            return null;
        } else if (obj instanceof SizedObject) {
            add(obj);
            return null;
        } else {
            PEIApi.LOG.warn("Unknown ingredient: {} ({})", obj, ClassUtils.getPackageCanonicalName(obj.getClass()));
            return null;
        }
        return new SizedObject<>(amount, obj);
    }

    public Map<Object, Integer> getMap() {
        return ingredients.getMap();
    }
}
