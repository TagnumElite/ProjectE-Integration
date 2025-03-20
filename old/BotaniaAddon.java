/*
 * Copyright (c) 2019-2024 TagnumElite
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

import com.tagnumelite.projecteintegration.api.Utils;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.recipe.*;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.item.BotaniaItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Look at brew recipes
public class BotaniaAddon {
    public static final String MODID = "botania";

    static String NAME(String name) {
        return "Botania" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BElvenTradeMapper extends ARecipeTypeMapper<ElvenTradeRecipe> {
        @Override
        public String getName() {
            return NAME("ElvenTrade");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.ELVEN_TRADE_TYPE;
        }

        @Override
        public NSSOutput getOutput(ElvenTradeRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BManaInfusionMapper extends ARecipeTypeMapper<ManaInfusionRecipe> {
        @Override
        public String getName() {
            return NAME("ManaInfusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.MANA_INFUSION_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BPetalMapper extends ARecipeTypeMapper<PetalApothecaryRecipe> {
        @Override
        public String getName() {
            return NAME("Petal");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.PETAL_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BPureDaisyMapper extends ARecipeTypeMapper<PureDaisyRecipe> {
        @Override
        public String getName() {
            return NAME("PureDaisy");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.PURE_DAISY_TYPE;
        }

        @Override
        public NSSOutput getOutput(PureDaisyRecipe recipe) {
            return new NSSOutput(recipe.getOutputState());
        }

        @Override
        public NSSInput getInput(PureDaisyRecipe recipe) {
            List<BlockState> matches = recipe.getInput().getDisplayed();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            boolean res;
            if (matches == null) {
                return null;
            } else if (matches.size() == 1) {
                //Handle this ingredient as a direct representation of the stack it represents
                res = Utils.addBlockToIngredientMap(ingredientMap, matches.get(0).getBlock());
                return new NSSInput(ingredientMap, fakeGroupMap, res);
            } else if (matches.size() > 1) {
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                List<Block> stacks = new ArrayList<>();

                for (BlockState match : matches) {
                    NormalizedSimpleStack nss = Utils.getNSSFromBlock(match.getBlock());
                    if (nss != null)
                        rawNSSMatches.add(nss);
                    stacks.add(match.getBlock());
                }

                int count = stacks.size();
                if (count == 1) {// I feel like this is unreachable code.... TODO: Unreachable Code?
                    res = Utils.addBlockToIngredientMap(ingredientMap, stacks.get(0).defaultBlockState().getBlock());
                    return new NSSInput(ingredientMap, fakeGroupMap, res);
                } else {
                    //Handle this ingredient as the representation of all the stacks it supports
                    Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                    NormalizedSimpleStack dummy = group.getA();
                    ingredientMap.addIngredient(dummy, 1);
                    if (group.getB()) {
                        //Only lookup the matching stacks for the group with conversion if we don't already have
                        // a group created for this dummy ingredient
                        // Note: We soft ignore cases where it fails/there are no matching group ingredients
                        // as then our fake ingredient will never actually have an emc value assigned with it
                        // so the recipe won't either
                        List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                        for (Block block : stacks) {
                            IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                            if (!Utils.addBlockToIngredientMap(groupIngredientMap, block))
                                continue;
                            groupIngredientMaps.add(groupIngredientMap);
                        }
                        fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                    }
                }
            }
            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BRuneAlterMapper extends ARecipeTypeMapper<RunicAltarRecipe> {
        @Override
        public String getName() {
            return NAME("RuneAlter");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.RUNE_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BTerraPlateMapper extends ARecipeTypeMapper<TerrestrialAgglomerationRecipe> {
        @Override
        public String getName() {
            return NAME("TerraPlate");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == BotaniaRecipeTypes.TERRA_PLATE_TYPE;
        }
    }

    @ConversionProvider(MODID)
    public static class BotaniaConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Botania")
                    .before(BotaniaItems.pebble, 1)
                    .before(BotaniaItems.livingroot, 1)
                    .before(BotaniaItems.lifeEssence, 256)
                    .before(BotaniaItems.enderAirBottle, 1024);
        }
    }
}
