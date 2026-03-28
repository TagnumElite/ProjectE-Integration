/*
 * Copyright (c) 2019-2026 TagnumElite
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

package com.tagnumelite.projecteintegration.addons;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.enderio.enderio.content.machines.painting.PaintingRecipe;
import com.enderio.enderio.content.machines.sag_mill.SagMillingRecipe;
import com.enderio.enderio.content.machines.slicer.SlicingRecipe;
import com.enderio.enderio.content.machines.soul_binder.SoulBindingRecipe;
import com.enderio.enderio.content.machines.vat.FermentingRecipe;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class EnderIOAddon {
    protected static final String MODID = "enderio";

    protected static String NAME(String name) {
        return "EnderIO" + name + "Mapper";
    }

    protected abstract static class EIOMachineMapper<R extends MachineRecipe<?>> extends ARecipeTypeMapper<R> {
        @Override
        public NSSOutput getOutput(R recipe) {
            List<OutputStack> results = recipe.getResultStacks(registryAccess);
            if (results.isEmpty()) return null;
            if (results.size() == 1) {
                OutputStack result = results.getFirst();
                if (result.isFluid()) return new NSSOutput(result.getFluid());
                return new NSSOutput(result.getItem());
            }
            return mapOutputs(results.stream().map(o -> o.isFluid() ? o.getFluid() : o.getItem()).toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOAlloySmeltingMapper extends EIOMachineMapper<AlloySmeltingRecipe> {
        @Override
        public String getName() {
            return NAME("AlloySmelting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.ALLOY_SMELTING.type().get();
        }

        @Override
        public NSSInput getInput(AlloySmeltingRecipe recipe) {
            return convertSizedIngredients(recipe.inputs());
        }
    }

    // We ignore Fire Crafting because we must

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOSagMillingMapper extends EIOMachineMapper<SagMillingRecipe> {
        @Override
        public String getName() {
            return NAME("SagMilling");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.SAG_MILLING.type().get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOSlicingMapper extends EIOMachineMapper<SlicingRecipe> {
        @Override
        public String getName() {
            return NAME("Slicing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.SLICING.type().get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOSoulBindingMapper extends EIOMachineMapper<SoulBindingRecipe> {
        @Override
        public String getName() {
            return NAME("SoulBinding");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.SOUL_BINDING.type().get();
        }

        // We skip the soul vials because those are returned & TODO: Look at making souls have emc
        @Override
        protected List<Ingredient> getIngredients(SoulBindingRecipe recipe) {
            return List.of(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(SoulBindingRecipe recipe) {
            return new NSSOutput(recipe.output());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOPaintingMapper extends EIOMachineMapper<PaintingRecipe> {
        @Override
        public String getName() {
            return NAME("Painting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.PAINTING.type().get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class EIOFermentingMapper extends EIOMachineMapper<FermentingRecipe> {
        @Override
        public String getName() {
            return NAME("Fermenting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == EIORecipes.VAT_FERMENTING.type().get();
        }

        @Override
        public NSSInput getInput(FermentingRecipe recipe) {
            return getInputBuilder().addFluid(recipe.input()).addItemKey(recipe.firstReagent())
                    .addItemKey(recipe.secondReagent()).build();
        }
    }

    @ConversionProvider(MODID)
    public static class EnderIOConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.before(EIOItems.GRAINS_OF_INFINITY, 4).before(EIOItems.SUSPICIOUS_SEED, 8)
                    .before(EIOBlocks.ENDERMAN_HEAD, 2048).conversion(EIOItems.SILICON).ingredient(Items.SAND)
                    .end(); // TODO: I don't like this conversion
        }
    }
}
