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

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;

public class AstralSorceryAddon {
    public static final String MODID = "astralsorcery";

    static String NAME(String name) {
        return "AstralSorcery" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASAltarMapper extends ARecipeTypeMapper<AltarRecipe> {
        @Override
        public String getName() {
            return NAME("Altar");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypesAS.ALTAR_CRAFTING_TYPE.get();
        }

        @Override
        public NSSInput getInput(AltarRecipe recipe) {
            AltarRecipeGrid grid = recipe.getGrid();
            NSSInput.Builder input = getInputBuilder();

            for (IngredientBridge ingredient : grid.getInputs()) {
                if (ingredient.test(FluidsAS.LIQUID_STARLIGHT.getBucket().toStack()))
                    continue; // We skip the liquid starlight buckets because I'm not assigning EMC to liquid starlight
                input.addIngredient(ingredient.getIngredient());
            }

            for (IngredientBridge relayIngredient : grid.getRelayInputs()) {
                input.addIngredient(relayIngredient.getIngredient());
            }

            return input.build();
        }

        @Override
        public NSSOutput getOutput(AltarRecipe recipe) {
            try {
                return mapOutputs(recipe.getOutputs().toArray());
            } catch (Exception e) {
                PEIntegration.LOGGER.info("Failed to get outputs for Altar Recipe", e);
                // TODO: This is an temporary replacement, must make sure to implement something proper.
                return null;
            }
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASFocalTransmutationMapper extends ARecipeTypeMapper<FocalTransmutationRecipe> {
        @Override
        public String getName() {
            return NAME("FocalTransmutation");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(FocalTransmutationRecipe recipe) {
            return new NSSOutput(recipe.getOutputForDisplay().getLast());
        }

        @Override
        protected List<Ingredient> getIngredients(FocalTransmutationRecipe recipe) {
            return Collections.singletonList(recipe.getInputDisplay());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASInfusionMapper extends ARecipeTypeMapper<InfusionRecipe> {
        @Override
        public String getName() {
            return NAME("Infusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypesAS.INFUSION_TYPE.get();
        }

        @Override
        public NSSOutput getOutput(InfusionRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }

        @Override
        protected List<Ingredient> getIngredients(InfusionRecipe recipe) {
            return Collections.singletonList(recipe.getItemInput());
        }
    }

    // NOTE: There is no LiquidInteraction mapper, just because it shouldn't be needed.

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASLightwellMapper extends ARecipeTypeMapper<LightwellRecipe> {
        @Override
        public String getName() {
            return NAME("Lightwell");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Disabled by default. Maps 1 Item to 1 bucket (1000)";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypesAS.LIGHTWELL_TYPE.get();
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public NSSOutput getOutput(LightwellRecipe recipe) {
            return new NSSOutput(new FluidStack(recipe.getGeneratedFluid(), 1000));
        }

        @Override
        protected List<Ingredient> getIngredients(LightwellRecipe recipe) {
            return Collections.singletonList(recipe.getInput());
        }
    }

    @ConversionProvider(MODID)
    public static class ASConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.conversion(ItemsAS.STARMETAL_INGOT).ingredient(Items.IRON_INGOT).end()
                    .conversion(ItemsAS.STARDUST).ingredient(ItemsAS.STARMETAL_INGOT).end()
                    .conversion(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL).ingredient(ItemsAS.CELESTIAL_CRYSTAL).end()
                    .before(gemTag("aquamarine"), 128)
                    .before(commonTag("marbles"), 32)
                    .before(tag(MODID, "infused_woods"), 32)
                    .before(ItemsAS.ROCK_CRYSTAL, 256)
                    .before(ItemsAS.CELESTIAL_CRYSTAL, 512);
        }
    }
}
