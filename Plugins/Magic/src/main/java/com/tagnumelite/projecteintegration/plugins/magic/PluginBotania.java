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
package com.tagnumelite.projecteintegration.plugins.magic;

import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;

import java.util.ArrayList;

import static net.minecraftforge.fluids.Fluid.BUCKET_VOLUME;

@PEIPlugin("botania")
public class PluginBotania extends APEIPlugin {
    private final Object mana = new Object();

    @Override
    public void setup() {
        addEMC("mana", mana, 1, "NOTE: This is effectively a multiplier. Calculation is {EMC VALUE} * {MANA REQUIRED}");

        addMapper(new ElvenTradeMapper());
        addMapper(new PetalMapper());
        addMapper(new PureDaisyMapper());
        addMapper(new ManaInfusionMapper());
        addMapper(new RuneAlterMapper());
    }

    private static class ElvenTradeMapper extends PEIMapper {
        public ElvenTradeMapper() {
            super("Elven Trade");
        }

        @Override
        public void setup() {
            BotaniaAPI.elvenTradeRecipes.forEach(r -> addRecipe(new ArrayList<>(r.getOutputs()), r.getInputs()));
        }
    }

    private static class PetalMapper extends PEIMapper {
        public PetalMapper() {
            super("Petal");
        }

        @Override
        public void setup() {
            BotaniaAPI.petalRecipes.forEach(r -> addRecipe(r.getOutput(), r.getInputs()));
        }
    }

    private static class PureDaisyMapper extends PEIMapper {
        public PureDaisyMapper() {
            super("Pure Daisy");
        }

        private static Object getStackFromBlockState(IBlockState state) {
            Block block = state.getBlock();
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid == null) {
                return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            } else {
                return new FluidStack(fluid, BUCKET_VOLUME);
            }
        }

        private static Object castInput(Object input) {
            if (input instanceof IBlockState)
                return getStackFromBlockState((IBlockState) input);
            else
                return input;
        }

        @Override
        public void setup() {
            for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
                Object output = getStackFromBlockState(recipe.getOutputState());
                if (output instanceof ItemStack)
                    addRecipe((ItemStack) output, castInput(recipe.getInput()));
                else
                    addRecipe((FluidStack) output, castInput(recipe.getInput()));
            }
        }
    }

    private class ManaInfusionMapper extends PEIMapper {
        public ManaInfusionMapper() {
            super("Mana Infusion");
        }

        @Override
        public void setup() {
            BotaniaAPI.manaInfusionRecipes.forEach(r -> addRecipe(r.getOutput(), r.getInput(), new SizedObject<>(r.getManaToConsume(), mana)));
        }
    }

    private class RuneAlterMapper extends PEIMapper {
        public RuneAlterMapper() {
            super("Rune Alter");
        }

        @Override
        public void setup() {
            BotaniaAPI.runeAltarRecipes.forEach(r -> addRecipe(r.getOutput(), r.getInputs(), new SizedObject<>(r.getManaUsage(), mana)));
        }
    }
}
