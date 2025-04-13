/*
 * Copyright (c) 2019-2022 TagnumElite
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

import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.blay09.mods.farmingforblockheads.api.Payment;
import net.blay09.mods.farmingforblockheads.recipe.MarketRecipe;
import net.blay09.mods.farmingforblockheads.recipe.ModRecipes;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class FarmingForBlockheadsAddon {
    @RecipeTypeMapper(requiredMods = "farmingforblockheads", priority = 1)
    public static class FFBMarketMapper extends ARecipeTypeMapper<MarketRecipe> {
        @Override
        public NSSInput getInput(MarketRecipe recipe) {
            Payment payment = recipe.getPaymentOrDefault();
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            convertIngredient(payment.count(), payment.ingredient(), ingMap, fakeGroupMap);

            return new NSSInput(ingMap, fakeGroupMap, true);
        }

        @Override
        public String getName() {
            return "FarmingForBlockheadsMarketMapper";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ModRecipes.marketRecipeType;
        }
    }
}
