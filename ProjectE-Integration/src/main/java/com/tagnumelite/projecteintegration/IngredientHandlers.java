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
package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.utils.IngredientHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraftforge.fluids.Fluid.BUCKET_VOLUME;

public final class IngredientHandlers {
    public static void registerHandlers() {
        IngredientHandler.registerHandler(new ItemStackHandler());
        IngredientHandler.registerHandler(new FluidStackHandler());
        IngredientHandler.registerHandler(new ItemHandler());
        IngredientHandler.registerHandler(new BlockHandler());
        IngredientHandler.registerHandler(new OreDictionaryHandler());
        IngredientHandler.registerHandler(new ObjectHandler());
        IngredientHandler.registerHandler(new IIngredientHandler());
        IngredientHandler.registerHandler(new BlockStateHandler());
        IngredientHandler.registerHandler(new SizedObjectHandler());
    }

    private static class ItemStackHandler implements IngredientHandler.Handler<ItemStack> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public SizedObject<Object> get(ItemStack obj) {
            if (obj.isEmpty()) return null;
            return new SizedObject<>(obj.getCount(), obj);
        }
    }

    private static class FluidStackHandler implements IngredientHandler.Handler<FluidStack> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public SizedObject<Object> get(FluidStack obj) {
            if (obj.amount == 0) return null;
            return new SizedObject<>(obj.amount, obj);
        }
    }

    private static class ItemHandler extends IngredientHandler.DefaultHandler<Item> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof Item;
        }
    }

    private static class BlockHandler extends IngredientHandler.DefaultHandler<Block> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof Block;
        }
    }

    private static class OreDictionaryHandler extends IngredientHandler.DefaultHandler<String> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof String;
        }
    }

    private static class ObjectHandler extends IngredientHandler.DefaultHandler<Object> {
        @Override
        public boolean check(Object obj) {
            return obj.getClass().equals(Object.class);
        }
    }

    private static class IIngredientHandler implements IngredientHandler.Handler<Ingredient> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof Ingredient;
        }

        @Override
        public SizedObject<Object> get(Ingredient obj) {
            if (obj == Ingredient.EMPTY) return null;
            return new SizedObject<>(1, PEIApi.getIngredient(obj));
        }
    }

    private static class BlockStateHandler implements IngredientHandler.Handler<IBlockState> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof IBlockState;
        }

        @Override
        public SizedObject<Object> get(IBlockState state) {
            Block block = state.getBlock();
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid == null) {
                return new SizedObject<>(1, new ItemStack(block, 1, block.getMetaFromState(state)));
            } else {
                return new SizedObject<>(BUCKET_VOLUME, new FluidStack(fluid, BUCKET_VOLUME));
            }
        }
    }

    private static class SizedObjectHandler implements IngredientHandler.Handler<SizedObject<?>> {
        @Override
        public boolean check(Object obj) {
            return obj instanceof SizedObject<?>;
        }

        @Override
        public SizedObject<Object> get(SizedObject<?> obj) {
            return new SizedObject<>(obj.amount, IngredientHandler.convert(obj.object));
        }
    }
}
