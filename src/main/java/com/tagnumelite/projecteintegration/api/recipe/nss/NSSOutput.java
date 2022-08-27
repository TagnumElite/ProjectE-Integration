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

import com.tagnumelite.projecteintegration.PEIntegration;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tagnumelite.projecteintegration.api.Utils.getDummyMap;

/**
 * A 'data' class to hold both a {@link NormalizedSimpleStack} and an integer denoting the output item and amount.
 */
public class NSSOutput {
    /**
     * A Singleton to denote an empty NSSOutput
     */
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

    /**
     * A helper constructor to create an NSSOutput from an {@link BlockState}
     *
     * @param state The {@link BlockState} to be converted
     */
    public NSSOutput(BlockState state) {
        this.amount = 1;
        this.nss = NSSItem.createItem(state.getBlock());
    }

    /**
     * A helper function to check this object has a valid NSS object with amount over 0 and isn't equal to EMPTY.
     *
     * @return A boolean value denoting that this object has something of value.
     */
    public boolean isEmpty() {
        return this == EMPTY || amount <= 0 || nss == null;
    }

    @Override
    public String toString() {
        return "NSSOutput{amount=" + amount + ";nss=" + nss + "}";
    }

    /**
     * NSSOutput.Builder is used to create a {@link NSSOutput} with multiple outputs.
     *
     * @TODO: Make a conversion module in {@link com.tagnumelite.projecteintegration.api.Utils}
     */
    public static class Builder {
        private final IMappingCollector<NormalizedSimpleStack, Long> mapper;
        private final INSSFakeGroupManager fakeGroupManager;
        private final ResourceLocation recipeID;
        private final HashMap<NormalizedSimpleStack, Integer> outputStacks;
        private int totalOutputs;

        /**
         * @param mapper           The mapping collector given by the RecipeMapper
         * @param fakeGroupManager The fake group manager given by the RecipeMapper
         * @param recipeID         The ID of the recipe being worked on
         */
        public Builder(IMappingCollector<NormalizedSimpleStack, Long> mapper, INSSFakeGroupManager fakeGroupManager, ResourceLocation recipeID) {
            this.mapper = mapper;
            this.fakeGroupManager = fakeGroupManager;
            this.recipeID = recipeID;
            this.outputStacks = new HashMap<>();
            this.totalOutputs = 0;
        }

        /**
         * Add an output to the mapper with all of its variants. Supported classes are {@link ItemStack} and {@link FluidStack}.
         *
         * @param variants The array of variants for an output
         */
        public void addOutput(Object... variants) {
            if (variants.length <= 0) return; // Skip empty lists
            this.addOutput(Arrays.asList(variants));
        }

        /**
         * Add an output to the mapper with all of its variants. Supported classes are {@link ItemStack} and {@link FluidStack}.
         *
         * @param variants The list of variants for an output
         */
        public void addOutput(List<?> variants) {
            if (variants == null || variants.isEmpty()) return; // Skip empty lists

            // Assume output stacks will be the size length as outputs
            Map<NormalizedSimpleStack, Integer> outputStacks = new HashMap<>(variants.size());

            for (Object variant : variants) {
                if (variant == null) continue;

                if (variant instanceof ItemStack item) {
                    if (item.isEmpty()) {
                        PEIntegration.debugLog("ItemStack ({}) came up empty during recipe '{}'", item, recipeID);
                        continue;
                    }

                    outputStacks.put(NSSItem.createItem(item), item.getCount());
                } else if (variant instanceof FluidStack fluid) {
                    if (fluid.isEmpty()) {
                        PEIntegration.debugLog("FluidStack ({}) came up empty during recipe '{}'", fluid, recipeID);
                        continue;
                    }

                    outputStacks.put(NSSFluid.createFluid(fluid), fluid.getAmount());
                } else {
                    PEIntegration.LOGGER.warn("Recipe ({}) has unsupported output variant: {}. Skipping...", recipeID, variant);
                }
            }

            NormalizedSimpleStack dummy = fakeGroupManager.getOrCreateFakeGroup(outputStacks.keySet()).getA();

            for (Map.Entry<NormalizedSimpleStack, Integer> entry : outputStacks.entrySet()) {
                mapper.addConversion(entry.getValue(), entry.getKey(), getDummyMap(dummy, 1));
            }

            this.outputStacks.put(dummy, 1);
            this.totalOutputs += 1;
        }

        /**
         * Add multiple outputs to the mapper. Supported classes are {@link ItemStack} and {@link FluidStack}.
         *
         * @param outputs An array of outputs to be added
         */
        public void addOutputs(Object... outputs) {
            if (outputs.length <= 0) return; // Skip empty lists
            this.addOutputs(Arrays.asList(outputs));
        }

        /**
         * Add multiple outputs to the mapper. Supported classes are {@link ItemStack} and {@link FluidStack}.
         *
         * @param outputs A list of outputs to be added
         */
        public void addOutputs(List<?> outputs) {
            if (outputs == null || outputs.isEmpty()) return; // Skip empty lists

            for (Object output : outputs) {
                if (output == null) continue;

                if (output instanceof ItemStack item) {
                    if (item.isEmpty()) {
                        PEIntegration.debugLog("ItemStack ({}) came up empty during recipe '{}'", item, recipeID);
                        continue;
                    }

                    outputStacks.put(NSSItem.createItem(item), item.getCount());
                    totalOutputs += item.getCount();
                } else if (output instanceof FluidStack fluid) {
                    if (fluid.isEmpty()) {
                        PEIntegration.debugLog("FluidStack ({}) came up empty during recipe '{}'", fluid, recipeID);
                        continue;
                    }

                    outputStacks.put(NSSFluid.createFluid(fluid), fluid.getAmount());
                    totalOutputs += fluid.getAmount();
                } else {
                    PEIntegration.LOGGER.warn("Recipe ({}) has unsupported output: {}. Skipping...", recipeID, output);
                }
            }
        }

        /**
         * Get the resulting {@link NSSOutput} from all the outputs.
         *
         * @return A NSSOutput resulting from the outputs or {@link NSSOutput#EMPTY} if it failed.
         */
        public NSSOutput toOutput() {
            if (totalOutputs <= 0 || outputStacks.isEmpty()) {
                PEIntegration.LOGGER.warn("NSSOutput.Builder resulted in {} outputs from recipe ({}): {}", totalOutputs, recipeID, outputStacks);
                return NSSOutput.EMPTY;
            }

            NormalizedSimpleStack dummy = fakeGroupManager.getOrCreateFakeGroup(outputStacks.keySet()).getA();

            for (Map.Entry<NormalizedSimpleStack, Integer> entry : outputStacks.entrySet()) {
                mapper.addConversion(entry.getValue(), entry.getKey(), getDummyMap(dummy, entry.getValue()));
            }

            return new NSSOutput(totalOutputs, dummy);
        }
    }
}
