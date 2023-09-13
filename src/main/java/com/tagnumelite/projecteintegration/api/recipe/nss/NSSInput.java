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

package com.tagnumelite.projecteintegration.api.recipe.nss;

import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class NSSInput {
    public final IngredientMap<NormalizedSimpleStack> ingredientMap;
    public final List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap;
    public final boolean successful;

    /**
     * @param ingredientMap
     * @param fakeGroupMap
     * @param successful
     */
    public NSSInput(IngredientMap<NormalizedSimpleStack> ingredientMap,
                    List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap,
                    boolean successful) {
        this.ingredientMap = ingredientMap;
        this.fakeGroupMap = fakeGroupMap;
        this.successful = successful;
    }

    /**
     * @param ingredientMap
     * @param successful
     */
    public NSSInput(IngredientMap<NormalizedSimpleStack> ingredientMap, boolean successful) {
        this(ingredientMap, new ArrayList<>(), successful);
    }

    //public static NSSInput createItem(ItemStack stack) {
    //
    //}

    public static NSSInput createFluid(FluidStack fluid) {
        IngredientMap<NormalizedSimpleStack> ingMap = new IngredientMap<>();
        ingMap.addIngredient(NSSFluid.createFluid(fluid), fluid.getAmount());
        return new NSSInput(ingMap, true);
    }

    /**
     * @return
     */
    public Map<NormalizedSimpleStack, Integer> getMap() {
        return ingredientMap.getMap();
    }
}
