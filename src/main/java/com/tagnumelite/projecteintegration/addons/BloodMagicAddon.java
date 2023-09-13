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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.FRandom;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import wayoftime.bloodmagic.common.item.BloodMagicItems;
import wayoftime.bloodmagic.common.recipe.BloodMagicRecipeType;
import wayoftime.bloodmagic.recipe.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BloodMagicAddon {
    public static final String MODID = "bloodmagic";

    static String NAME(String name) {
        return "BloodMagic" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BMAlchemyTableMapper extends ARecipeTypeMapper<RecipeAlchemyTable> {
        @Override
        public String getName() {
            return NAME("AlchemyTable");
        }

        @Override
        public NSSOutput getOutput(RecipeAlchemyTable recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeAlchemyTable recipe) {
            return recipe.getInput();
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BloodMagicRecipeType.ALCHEMYTABLE.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BMAlchemyArrayMapper extends ARecipeTypeMapper<RecipeAlchemyArray> {
        @Override
        public String getName() {
            return NAME("AlchemyArray");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BloodMagicRecipeType.ARRAY.get();
        }

        @Override
        public NSSOutput getOutput(RecipeAlchemyArray recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1) // Untested
    public static class BMARCMapper extends ARecipeTypeMapper<RecipeARC> {
        @Override
        public String getName() {
            return NAME("AlchemicalReactionChamber");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BloodMagicRecipeType.ARC.get();
        }

        @Override
        public NSSOutput getOutput(RecipeARC recipe) {
            List<Object> outputs = new ArrayList<>();
            outputs.add(recipe.getFluidOutput());
            outputs.addAll(recipe.getAllOutputs(new FRandom(1), ItemStack.EMPTY, ItemStack.EMPTY, 0));
            return mapOutputs(outputs.toArray());
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeARC recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSInput getInput(RecipeARC recipe) {
            // TODO: Add support for recipe.getFluidIngredient()
            return super.getInput(recipe);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BMBloodAltarMapper extends ARecipeTypeMapper<RecipeBloodAltar> {
        @Override
        public String getName() {
            return NAME("BloodAltar");
        }

        // You know, I could make blood have emc. TODO: Do I want blood to have emc.
        // To whom may not realise the implications, it means I can calculate your bloods worth in your body. Creepy

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BloodMagicRecipeType.ALTAR.get();
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeBloodAltar recipe) {
            return Collections.singletonList(recipe.getInput());
        }

        @Override
        public NSSOutput getOutput(RecipeBloodAltar recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BMTartaricForgeMapper extends ARecipeTypeMapper<RecipeTartaricForge> {
        @Override
        public String getName() {
            return NAME("TartaricForge");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BloodMagicRecipeType.TARTARICFORGE.get();
        }

        // OOH, souls could have emc. Mine would probably be -1 because, no one want dat sh**.

        @Override
        public NSSOutput getOutput(RecipeTartaricForge recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeTartaricForge recipe) {
            return recipe.getInput();
        }
    }

    @ConversionProvider(MODID)
    public static class BloodMagicConversion extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default recipe conversions for Blood Magic")
                    .before(BloodMagicItems.WEAK_BLOOD_SHARD.get(), 256)
                    .before(tag("bloodmagic:crystals/demon"), 512);
            // I'm ignoring demon wills on purpose.
        }
    }
}
