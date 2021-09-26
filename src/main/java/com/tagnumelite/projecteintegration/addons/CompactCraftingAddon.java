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

import com.robotgryphon.compactcrafting.Registration;
import com.robotgryphon.compactcrafting.api.components.IRecipeBlockComponent;
import com.robotgryphon.compactcrafting.recipes.MiniaturizationRecipe;
import com.robotgryphon.compactcrafting.recipes.components.BlockComponent;
import com.tagnumelite.projecteintegration.api.recipe.APEIRecipeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Tuple;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CompactCraftingAddon {
    @RecipeTypeMapper(requiredMods = {"compactcrafting"}, priority = 1)
    public static class CCMiniaturizationMapper extends APEIRecipeMapper<MiniaturizationRecipe> {
        @Override
        public String getName() {
            return "CompactCraftingMiniaturizationMapper";
        }

        @Override
        public String getDescription() {
            return "Mapper for Compact Crafting miniaturization recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == Registration.MINIATURIZATION_RECIPE_TYPE;
        }

        @Override
        public boolean convertRecipe(MiniaturizationRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            ItemStack catalyst = recipe.getCatalyst().copy();
            ingredientMap.addIngredient(NSSItem.createItem(catalyst), /* Ensure 1 */Math.max(catalyst.getCount(), 1));

            recipe.getRecipeComponentTotals().entrySet().stream().filter(comp -> comp.getValue() > 0)
                    .forEach((comp) -> {
                        String componentKey = comp.getKey();
                        int required = comp.getValue();

                        Optional<IRecipeBlockComponent> component = recipe.getRecipeBlockComponent(componentKey);
                        if (component.isPresent()) {
                            IRecipeBlockComponent iBlockComponent = component.get();
                            if (iBlockComponent instanceof BlockComponent) {
                                BlockComponent blockComponent = (BlockComponent) iBlockComponent;
                                Item blockItem = blockComponent.getBlock().asItem();
                                if (blockItem != Items.AIR)
                                    ingredientMap.addIngredient(NSSItem.createItem(new ItemStack(blockItem)), required);
                            }
                        }
                    });

            ItemStack[] outputs = recipe.getOutputs();
            if (outputs.length == 0) {
                return false;
            } else if (outputs.length == 1) {
                ItemStack output = outputs[0];
                mapper.addConversion(output.getCount(), NSSItem.createItem(output), ingredientMap.getMap());
                return true;
            } else {
                // If the recipe contains multiple outputs, here comes the special stuff.
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                for (ItemStack output : outputs) {
                    if (!output.isEmpty()) {
                        rawNSSMatches.add(NSSItem.createItem(output));
                    }
                }
                Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                NormalizedSimpleStack nssFake = group.getA();

                IngredientMap<NormalizedSimpleStack> outputInputIng = new IngredientMap<>();
                outputInputIng.addIngredient(nssFake, 1);
                Map<NormalizedSimpleStack, Integer> outputInputMap = outputInputIng.getMap();

                for (ItemStack output : outputs) {
                    mapper.addConversion(output.getCount(), NSSItem.createItem(output), outputInputMap);
                }
                // TODO: Decide whether it better to outputs.length or total outputs for each conversions
                mapper.addConversion(outputs.length, nssFake, ingredientMap.getMap());
                return true;
            }
        }
    }
}
