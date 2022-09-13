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
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: WHAT THE **** is this ****? But really tho, why old me why.
/**
 * A utility class to handle recipe ingredients.
 * <p>
 * Uses a {@link ArrayList} of {@link Handler} to convert {@link Object} to ingredients that can be converted into a {@link IngredientMap}
 * to be used for inputs in {@link moze_intel.projecte.api.proxy.IConversionProxy#addConversion(int, Object, Map)}.
 * <p>
 * To register a handler just use {@link #registerHandler(Handler)}.
 */
public final class IngredientHandler {
    private static final List<Handler<?>> HANDLERS = new ArrayList<>();
    private final IngredientMap<Object> ingredients = new IngredientMap<>();

    /**
     * Create an IngredientHandler that will convert objects into something that
     * {@link moze_intel.projecte.api.proxy.IConversionProxy#addConversion(int, Object, Map)} can use to add conversions.
     * <p>
     * Uses {@link Handler}s for conversion.
     */
    public IngredientHandler() {
    }

    /**
     *
     * @param handler The handler to register
     * @return A boolean value whether the handler was registered
     */
    public static boolean registerHandler(Handler<?> handler) {
        return HANDLERS.add(handler);
    }

    /**
     * Clears the {@code HANDLERS} map of handlers, useful for memory cleanup
     */
    public static void clearHandlers() {
        HANDLERS.clear();
    }

    /**
     * If {@code obj} is null then return will be null.
     *
     * @param obj The object to be converted
     * @return The SizedObject that contains the Class/Object that will be used by ProjectE
     * @throws IllegalStateException There is not Handler registered for the object
     */
    public static SizedObject<Object> convert(Object obj) throws IllegalStateException {
        if (obj == null) return null;

        // TODO: Figure out a faster way to do this. This could be and probably is detrimental to performance.
        for (Handler<?> handler : HANDLERS) {
            if (handler.check(obj)) {
                return handler.convert(obj);
            }
        }

        throw new IllegalStateException("There is no Handler registered for: {" + obj.getClass() + "} " + obj);
    }

    /**
     * @param ingredients An array of objects to be added to the {@link IngredientMap}
     * @return {@code true} if all ingredients were added or {@code false} if one or all ingredients failed to be added.
     * @see #add(Object)
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

        SizedObject<Object> object;
        if (ingredient instanceof List<?>) {
            // Lists are handled differently
            List<?> list = (List<?>) ingredient;
            // If list is empty, just return false
            if (list.isEmpty()) return false;
            // There is only one item in the list, just return that
            if (list.size() == 1) return add(list.get(0));
            if (list instanceof InputList) {
                // List is InputList, that means it is a list of potential inputs for a single object
                object = new SizedObject<>(1, PEIApi.getList(list));
            } else {
                // List is just normal, therefor just add everything.
                return addAll(list.toArray());
            }
        } else if (ingredient instanceof Object[]) {
            // We add everything from arrays as another ingredient
            return addAll((Object[]) ingredient);
        } else {
            try {
                // Convert everything else
                object = convert(ingredient);
            } catch (IllegalStateException e) {
                PEIApi.LOGGER.warn("Ingredient '{}' ({}) doesn't have an handler", ingredient,
                    ClassUtils.getPackageCanonicalName(ingredient.getClass()), e);
                object = null;
            }
        }
        if (object == null) {
            PEIApi.LOGGER.warn("Unknown ingredient: {} ({})", ingredient, ClassUtils.getPackageCanonicalName(ingredient.getClass()));
            return false;
        } else if (object.object == null) {
            PEIApi.LOGGER.warn("SizedObject ingredient is null: {} ({})", ingredient, ClassUtils.getPackageCanonicalName(ingredient.getClass()));
            return false;
        } else if (object.amount <= 0) {
            PEIApi.LOGGER.warn("SizedObject amount is ({} <= 0): {} ({})", object.amount, ingredient, ClassUtils.getPackageCanonicalName(object.object.getClass()));
            return false;
        }
        ingredients.addIngredient(object.object, object.amount);
        return true;
    }

    /**
     * @return The map of objects and their amounts for conversion input
     */
    public Map<Object, Integer> getMap() {
        return ingredients.getMap();
    }

    /**
     * An interface for handling ingredient conversion.
     *
     * @param <T> The class to be handled, allows for easy conversion.
     */
    public interface Handler<T> {
        boolean check(Object obj);

        /**
         * @param obj The object to be converted into a {@link SizedObject}
         * @return A SizedObject
         * @throws ClassCastException The wrong class was handled by this handler.
         */
        @SuppressWarnings("unchecked")
        default SizedObject<Object> convert(Object obj) throws ClassCastException {
            return get((T) obj);
        }

        SizedObject<Object> get(T obj);
    }

    /**
     * Default ingredient conversion, just results in a new {@link SizedObject} of amount '1' and input object.
     */
    public static abstract class DefaultHandler<T> implements Handler<T> {
        @Override
        public SizedObject<Object> get(T obj) {
            return new SizedObject<>(1, obj);
        }
    }
}
