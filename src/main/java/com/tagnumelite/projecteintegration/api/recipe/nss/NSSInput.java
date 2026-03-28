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

import com.tagnumelite.projecteintegration.api.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.utils.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A record for holding data regarding ingredientMaps, fake groups and its successfulness
 *
 * @param ingredientMap A map/counter of objects to be processed
 * @param fakeGroupMap  A list of fake groups to be processed
 * @param successful    A boolean stating all conversions where successful
 */
public record NSSInput(Object2IntMap<NormalizedSimpleStack> ingredientMap,
                       List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap,
                       boolean successful) {
    public NSSInput(Object2IntMap<NormalizedSimpleStack> ingredientMap, boolean successful) {
        this(ingredientMap, new ArrayList<>(), successful);
    }

    @Deprecated(since = "8.3.0", forRemoval = true)
    public static NSSInput createFluid(FluidStack fluid) {
        return ofFluid(fluid);
    }

    /**
     * Utility method to create a NSSInput from a single FluidStack
     *
     * @param fluid The FluidStack to be turned into an NSSInput
     * @return A NSSInput with the single FluidStack
     */
    public static NSSInput ofFluid(FluidStack fluid) {
        Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
        ingMap.mergeInt(NSSFluid.createFluid(fluid), fluid.getAmount(), Constants.INT_SUM);
        return new NSSInput(ingMap, true);
    }

    @Deprecated(since = "8.3.0", forRemoval = true)
    public static NSSInput createItem(ItemStack item) {
        return ofItem(item);
    }

    /**
     * Utility method to create a NSSInput from a single ItemStack
     *
     * @param item The ItemStack to be converted into a NSSInput
     * @return The NSSInput with the single ItemStack
     */
    public static NSSInput ofItem(ItemStack item) {
        Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
        ingMap.mergeInt(NSSItem.createItem(item), item.getCount(), Constants.INT_SUM);
        return new NSSInput(ingMap, true);
    }

    public Object2IntMap<NormalizedSimpleStack> getMap() {
        return ingredientMap;
    }

    /**
     *
     */
    public static class Builder {
        private final Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();
        private final List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
        private final IMappingCollector<NormalizedSimpleStack, Long> mapper;
        private final INSSFakeGroupManager fakeGroupManager;
        private final ResourceLocation recipeID;
        private boolean successful = true;

        public Builder(IMappingCollector<NormalizedSimpleStack, Long> mapper, INSSFakeGroupManager fakeGroupManager,
                       ResourceLocation recipeID) {
            this.mapper = mapper;
            this.fakeGroupManager = fakeGroupManager;
            this.recipeID = recipeID;
        }

        public Builder addItem(ItemStack item) {
            ingredientMap.put(NSSItem.createItem(item), item.getCount());
            return this;
        }

        public Builder addItemKey(TagKey<Item> itemTagKey) {
            return addIngredient(Ingredient.of(itemTagKey));
        }

        public Builder addIngredient(Ingredient ingredient) {
            return addIngredient(1, ingredient);
        }

        public Builder addIngredient(int amount, Ingredient ingredient) {
            success(Utils.convertIngredient(amount, ingredient, ingredientMap, fakeGroupMap, fakeGroupManager,
                    recipeID.toString()));
            return this;
        }

        public Builder addSizedIngredient(SizedIngredient ingredient) {
            return addIngredient(ingredient.count(), ingredient.ingredient());
        }

        public Builder addFluid(FluidStack fluidStack) {
            ingredientMap.put(NSSFluid.createFluid(fluidStack), fluidStack.getAmount());
            return this;
        }

        public Builder addFluid(FluidIngredient fluidIngredient) {
            return addFluid(1, fluidIngredient);
        }

        public Builder addFluid(int amount, FluidIngredient fluidIngredient) {
            success(Utils.convertFluidIngredient(amount, Arrays.asList(fluidIngredient.getStacks()), ingredientMap,
                    fakeGroupMap, fakeGroupManager, recipeID.toString()));
            return this;
        }

        public Builder addFluid(SizedFluidIngredient sizedFluidIngredient) {
            return addFluid(sizedFluidIngredient.amount(), sizedFluidIngredient.ingredient());
        }

        public Builder addFluid(int amount, List<FluidStack> fluidStacks) {
            return addFluid(amount, FluidIngredient.of(fluidStacks.toArray(new FluidStack[0])));
        }

        public NSSInput build() {
            return new NSSInput(ingredientMap, fakeGroupMap, successful);
        }

        private void success(boolean successful) {
            if (!successful && this.successful) {
                this.successful = false;
            }
        }
    }
}
