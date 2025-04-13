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

package com.tagnumelite.projecteintegration.api.recipe.nss;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.utils.Constants;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NSSInput {
    public final Object2IntMap<NormalizedSimpleStack> ingredientMap;
    public final List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap;
    public final boolean successful;

    /**
     * @param ingredientMap
     * @param fakeGroupMap
     * @param successful
     */
    public NSSInput(Object2IntMap<NormalizedSimpleStack> ingredientMap,
                    List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap,
                    boolean successful) {
        this.ingredientMap = ingredientMap;
        this.fakeGroupMap = fakeGroupMap;
        this.successful = successful;
    }

    /**
     * @param ingredientMap
     * @param successful
     */
    public NSSInput(Object2IntMap<NormalizedSimpleStack> ingredientMap, boolean successful) {
        this(ingredientMap, new ArrayList<>(), successful);
    }

    //public static NSSInput createItem(ItemStack stack) {
    //
    //}

    public static NSSInput createFluid(FluidStack fluid) {
        Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
        ingMap.mergeInt(NSSFluid.createFluid(fluid), fluid.getAmount(), Constants.INT_SUM);
        return new NSSInput(ingMap, true);
    }

    public static NSSInput createItem(ItemStack item) {
        Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
        ingMap.mergeInt(NSSItem.createItem(item), item.getCount(), Constants.INT_SUM);
        return new NSSInput(ingMap, true);
    }

    /**
     * @return
     */
    public Object2IntMap<NormalizedSimpleStack> getMap() {
        return ingredientMap;
    }
}
