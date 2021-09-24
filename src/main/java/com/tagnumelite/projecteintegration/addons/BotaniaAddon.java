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

import com.tagnumelite.projecteintegration.api.AConversionProvider;
import com.tagnumelite.projecteintegration.api.APEIRecipeMapper;
import com.tagnumelite.projecteintegration.api.ConversionProvider;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.botania.api.recipe.*;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.item.ModItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Look at brew recipes
public class BotaniaAddon {
    public static final String MODID = "botania";

    static String NAME(String name) {
        return "Botania"+name+"Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BElvenTradeMapper extends APEIRecipeMapper<IElvenTradeRecipe> {
        @Override
        public String getName() {
            return NAME("ElvenTrade");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.ELVEN_TRADE_TYPE;
        }

        @Override
        protected NSSOutput getOutput(IElvenTradeRecipe recipe) {
            return mapOutputs(recipe.getOutputs().toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BManaInfusionMapper extends APEIRecipeMapper<IManaInfusionRecipe> {
        @Override
        public String getName() {
            return NAME("ManaInfusion");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.MANA_INFUSION_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BPetalMapper extends APEIRecipeMapper<IPetalRecipe> {
        @Override
        public String getName() {
            return NAME("Petal");
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.PETAL_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BPureDaisyMapper extends APEIRecipeMapper<IPureDaisyRecipe> {
        @Override
        public String getName() {
            return NAME("PureDaisy");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.PURE_DAISY_TYPE;
        }

        @Override
        protected NSSOutput getOutput(IPureDaisyRecipe recipe) {
            return new NSSOutput(recipe.getOutputState());
        }

        @Override
        protected NSSInput getInput(IPureDaisyRecipe recipe) {
            List<BlockState> matches = recipe.getInput().getDisplayed();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            if (matches == null) {
                return null;
            } else if (matches.size() == 1) {
                //Handle this ingredient as a direct representation of the stack it represents
                ingredientMap.addIngredient(NSSItem.createItem(matches.get(0).getBlock()), 1);
                return new NSSInput(ingredientMap, fakeGroupMap, true);
            } else if (matches.size() > 1) {
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                List<Block> stacks = new ArrayList<>();

                for (BlockState match : matches) {
                    rawNSSMatches.add(NSSItem.createItem(match.getBlock()));
                    stacks.add(match.getBlock());
                }

                int count = stacks.size();
                if (count == 1) {// I feel like this is unreachable code.... TODO: Unreachable Code?
                    ingredientMap.addIngredient(NSSItem.createItem(stacks.get(0).getBlock()), 1);
                    return new NSSInput(ingredientMap, fakeGroupMap, true);
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
                        for (Block stack : stacks) {
                            IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                            groupIngredientMap.addIngredient(NSSItem.createItem(stack), 1);
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
    public static class BRuneAlterMapper extends APEIRecipeMapper<IRuneAltarRecipe> {
        @Override
        public String getName() {
            return NAME("RuneAlter");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.RUNE_TYPE;
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class BTerraPlateMapper extends APEIRecipeMapper<ITerraPlateRecipe> {
        @Override
        public String getName() {
            return NAME("TerraPlate");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == ModRecipeTypes.TERRA_PLATE_TYPE;
        }
    }

    @ConversionProvider(MODID)
    public static class BotaniaConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Botania")
                    .before(ModItems.pebble, 1)
                    .before(ModItems.livingroot, 1)
                    .before(ModItems.lifeEssence, 256)
                    .before(ModItems.enderAirBottle, 1024);
        }
    }
}
