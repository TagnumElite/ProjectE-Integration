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

import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import de.ellpeck.actuallyadditions.api.ActuallyTags;
import de.ellpeck.actuallyadditions.mod.crafting.*;
import de.ellpeck.actuallyadditions.mod.items.ActuallyItems;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class ActuallyAdditionsAddon {
    public static final String MODID = "actuallyadditions";

    public static String NAME(String name) {
        return "ActuallyAdditions" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AACrushingMapper extends ARecipeTypeMapper<CrushingRecipe> {
        @Override
        public String getName( ) {
            return NAME("Crushing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ActuallyRecipes.Types.CRUSHING.get();
        }

        @Override
        protected List<Ingredient> getIngredients(CrushingRecipe recipe) {
            return List.of(recipe.getInput());
        }

        // For now, getResultItem gets the first stack which is good enough for now
        // We don't care about chanced outputs
    }

    // Skip CoffeeMachineIngredient

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AAEmpowerMapper extends ARecipeTypeMapper<EmpowererRecipe> {
        @Override
        public String getName( ) {
            return NAME("Empowerer");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ActuallyRecipes.Types.EMPOWERING.get();
        }

        @Override
        protected List<Ingredient> getIngredients(EmpowererRecipe recipe) {
            return List.of(recipe.getInput(), recipe.getStandOne(), recipe.getStandTwo(), recipe.getStandThree(),
                           recipe.getStandFour());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AAFermentingMapper extends ARecipeTypeMapper<FermentingRecipe> {
        @Override
        public String getName( ) {
            return NAME("Fermenting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ActuallyRecipes.Types.FERMENTING.get();
        }

        @Override
        public NSSOutput getOutput(FermentingRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        public NSSInput getInput(FermentingRecipe recipe) {
            //TODO: Eventually this will become a FluidIngredient, fix then
            return NSSInput.ofFluid(recipe.getInput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AALaserMapper extends ARecipeTypeMapper<LaserRecipe> {
        @Override
        public String getName( ) {
            return NAME("Laser");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ActuallyRecipes.Types.LASER.get();
        }

        @Override
        protected List<Ingredient> getIngredients(LaserRecipe recipe) {
            return List.of(recipe.getInput());
        }
    }

    // Skip the mining laser, no weighted recipes please
    //@RecipeTypeMapper(requiredMods =  MODID, priority = 1)
    //public static class AAMiningLensMapper extends ARecipeTypeMapper<MiningLensRecipe> {
    //    @Override
    //    public String getName( ) {
    //        return NAME("MiningLens");
    //    }
    //
    //    @Override
    //    public boolean canHandle(RecipeType<?> recipeType) {
    //        return recipeType == ActuallyRecipes.Types.MINING_LENS.get();
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AAPressingMapper extends ARecipeTypeMapper<PressingRecipe> {
        @Override
        public String getName( ) {
            return NAME("Pressing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ActuallyRecipes.Types.PRESSING.get();
        }

        @Override
        public NSSOutput getOutput(PressingRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @ConversionProvider(MODID)
    public static class AAConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Actually Additions")
                   .before(ActuallyTags.Items.GEMS_BLACK_QUARTZ, 16).before(ActuallyTags.Items.COFFEE_BEANS, 4)
                   .before(ActuallyItems.CANOLA, 4).before(ActuallyItems.BATS_WING, 16)
                   .conversion(ActuallyItems.WATER_BOWL).ingredient(Items.BOWL).end();
        }
    }
}
