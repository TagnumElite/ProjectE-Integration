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

package com.tagnumelite.projecteintegration.addons;

import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.APEIRecipeMapper;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.BlockTransmutation;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AstralSorceryAddon {
    public static final String MODID = "astralsorcery";

    static String NAME(String name) {
        return "AstralSorcery"+name+"Mapper";
    }

    static String DESC(String name) {
        return "A recipe mapper for Astral Sorcery " + name + " recipes.";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASAltarMapper extends APEIRecipeMapper<SimpleAltarRecipe> {
        @Override
        public String getName() {
            return NAME("Altar");
        }

        @Override
        public String getDescription() {
            return DESC("Altar");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypesAS.TYPE_ALTAR.getType();
        }

        @Override
        protected NSSInput getInput(SimpleAltarRecipe recipe) {
            AltarRecipeGrid grid = recipe.getInputs();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            for (int slot = 0; slot < AltarRecipeGrid.MAX_INVENTORY_SIZE; slot++) {
                convertIngredient(grid.getIngredient(slot), ingredientMap, fakeGroupMap);
            }

            for (WrappedIngredient relayInput : recipe.getRelayInputs()) {
                convertIngredient(relayInput.getIngredient(), ingredientMap, fakeGroupMap);
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        protected NSSOutput getOutput(SimpleAltarRecipe recipe) {
            return new NSSOutput(recipe.getOutputForRender(Collections.emptyList()));
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASBlockTransmutationMapper extends APEIRecipeMapper<BlockTransmutation> {
        @Override
        public String getName() {
            return NAME("BlockTransmutation");
        }

        @Override
        public String getDescription() {
            return DESC("Block Transmutation");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypesAS.TYPE_BLOCK_TRANSMUTATION.getType();
        }

        @Override
        protected NSSOutput getOutput(BlockTransmutation recipe) {
            return new NSSOutput(recipe.getOutputDisplay());
        }

        @Override
        protected List<Ingredient> getIngredients(BlockTransmutation recipe) {
            return Collections.singletonList(Ingredient.of(recipe.getInputDisplay().stream()));
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASLiquidInfusionMapper extends APEIRecipeMapper<LiquidInfusion> {
        @Override
        public String getName() {
            return NAME("LiquidInfusion");
        }

        @Override
        public String getDescription() {
            return DESC("Liquid Infusion");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypesAS.TYPE_INFUSION.getType();
        }

        @Override
        protected NSSOutput getOutput(LiquidInfusion recipe) {
            return new NSSOutput(recipe.getOutputForRender(Collections.emptyList()));
        }

        @Override
        protected List<Ingredient> getIngredients(LiquidInfusion recipe) {
            return Collections.singletonList(recipe.getItemInput());
        }
    }

    // NOTE: There is no LiquidInteraction mapper, just because it shouldn't be needed.

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ASWellLiquefactionMapper extends APEIRecipeMapper<WellLiquefaction> {
        @Override
        public String getName() {
            return NAME("WellLiquefaction");
        }

        @Override
        public String getDescription() {
            return DESC("Well Liquefaction")+ " Disabled by default. Maps 1 Item to 1 bucket (" + FluidAttributes.BUCKET_VOLUME + ')';
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypesAS.TYPE_WELL.getType();
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        protected NSSOutput getOutput(WellLiquefaction recipe) {
            return new NSSOutput(new FluidStack(recipe.getFluidOutput(), FluidAttributes.BUCKET_VOLUME));
        }

        @Override
        protected List<Ingredient> getIngredients(WellLiquefaction recipe) {
            return Collections.singletonList(recipe.getInput());
        }
    }

    @ConversionProvider(MODID)
    public static class ASConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("default conversions for Astral Sorcery")
                    .before(gemTag("aquamarine"), 128)
                    .before(tag("forge:marble"), 32)
                    .before(BlocksAS.MARBLE_RAW, 32)
                    .before(ItemsAS.ROCK_CRYSTAL, 256)
                    .before(ItemsAS.CELESTIAL_CRYSTAL, 512);
        }
    }
}
