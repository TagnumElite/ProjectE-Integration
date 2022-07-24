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

import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * A 'data' class to hold both a {@link NormalizedSimpleStack} and an integer denoting the output item and amount.
 */
public class NSSOutput {
    public static final NSSOutput EMPTY = new NSSOutput(0, null);

    public final NormalizedSimpleStack nss;
    public final int amount;

    /**
     * Create an NSSOutput with the specified amount and NSS
     *
     * @param amount The amount that will be outputted
     * @param nss    The item that will be outputted
     */
    public NSSOutput(int amount, NormalizedSimpleStack nss) {
        this.amount = amount;
        this.nss = nss;
    }

    /**
     * A helper constructor to create an NSSOutput from an {@link ItemStack}
     *
     * @param item The {@link ItemStack} to be converted
     */
    public NSSOutput(ItemStack item) {
        this(item, false);
    }

    /**
     * A helper constructor to create an NSSOutput from an {@link ItemStack}.
     *
     * @param item         The {@link ItemStack} to be converted
     * @param forceCopyNBT Force copies the NBT tag from ItemStack or just uses {@link NSSItem#createItem(ItemStack)}
     */
    public NSSOutput(ItemStack item, boolean forceCopyNBT) {
        this.amount = item.getCount();
        if (forceCopyNBT) {
            this.nss = NSSItem.createItem(item.getItem(), item.getTag());
        } else {
            this.nss = NSSItem.createItem(item);
        }
    }

    /**
     * A helper constructor to create an NSSOutput from an {@link FluidStack}
     *
     * @param fluid The {@link FluidStack} to be converted
     */
    public NSSOutput(FluidStack fluid) {
        this.amount = fluid.getAmount();
        this.nss = NSSFluid.createFluid(fluid);
    }

    public NSSOutput(BlockState state) {
        this.amount = 1;
        this.nss = NSSItem.createItem(state.getBlock());
    }

    public boolean isEmpty() {
        return amount == 0 || this == EMPTY;
    }
}
