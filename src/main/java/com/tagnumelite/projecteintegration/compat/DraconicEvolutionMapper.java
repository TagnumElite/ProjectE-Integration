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

package com.tagnumelite.projecteintegration.compat;

import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.tagnumelite.projecteintegration.api.APEIRecipeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RecipeTypeMapper(requiredMods = "draconicevolution", priority = 1)
public class DraconicEvolutionMapper extends APEIRecipeMapper<FusionRecipe> {
    @Override
    public String getName() {
        return "DraconicEvolutionFusionMapper";
    }

    @Override
    public String getDescription() {
        return "Recipe mapper for Draconic Evolution Fusion recipes";
    }

    @Override
    public boolean canHandle(IRecipeType<?> iRecipeType) {
        return iRecipeType == DraconicAPI.FUSION_RECIPE_TYPE;
    }

    @Override
    protected List<Ingredient> getIngredients(FusionRecipe recipe) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(recipe.getCatalyst());
        ingredients.addAll(recipe.fusionIngredients().stream()
                .filter(IFusionRecipe.IFusionIngredient::consume)
                .map(IFusionRecipe.IFusionIngredient::get)
                .collect(Collectors.toList()));
        return ingredients;
    }
}
