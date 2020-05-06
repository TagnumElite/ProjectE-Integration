/*
 * Copyright (c) 2019-2020 TagnumElite
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
package com.tagnumelite.projecteintegration.plugins.magic;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.*;
import java.util.Map.Entry;

@PEIPlugin("thaumcraft")
public class PluginThaumcraft extends APEIPlugin {
    private final Map<Aspect, Object> ASPECT_MAP = new HashMap<>();
    private final Set<IArcaneRecipe> ARCANE_RECIPES = new HashSet<>();
    private final Set<CrucibleRecipe> CRUCIBLE_RECIPES = new HashSet<>();
    private final Set<InfusionRecipe> INFUSION_RECIPES = new HashSet<>();

    @Override
    public void setup() {
        for (Aspect aspect : Aspect.aspects.values()) {
            Object obj = new Object();
            addEMC("aspect_" + aspect.getName(), obj, 10);
            ASPECT_MAP.put(aspect, obj);
        }

        for (IRecipe recipe : CraftingManager.REGISTRY) {
            if (recipe instanceof IArcaneRecipe)
                ARCANE_RECIPES.add((IArcaneRecipe) recipe);
        }

        for (IThaumcraftRecipe recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe instanceof IArcaneRecipe)
                ARCANE_RECIPES.add((IArcaneRecipe) recipe);
            if (recipe instanceof CrucibleRecipe)
                CRUCIBLE_RECIPES.add((CrucibleRecipe) recipe);
            if (recipe instanceof InfusionRecipe)
                INFUSION_RECIPES.add((InfusionRecipe) recipe);
        }

        addMapper(new ArcaneMapper());
        addMapper(new CrucibleMapper());
        addMapper(new InfusionMapper());
    }

    private abstract class ThaumcraftMapper extends PEIMapper {
        public ThaumcraftMapper(String name) {
            super(name);
        }

        protected void addRecipe(Object recipe_output, Object recipe_input, AspectList recipe_aspects) {
            if (recipe_output == null || recipe_input == null || recipe_aspects == null)
                return;

            Object output = null;
            if (recipe_output instanceof ItemStack || recipe_output instanceof Item || recipe_output instanceof Block)
                output = recipe_output;
            else
                return;

            IngredientMap<Object> ingredients = new IngredientMap<Object>();
            if (recipe_input instanceof ItemStack || recipe_input instanceof Item || recipe_input instanceof Block)
                ingredients.addIngredient(recipe_input, 1);
            else if (recipe_input instanceof List) {
                for (Object in : (List<?>) recipe_input) {
                    if (in instanceof ItemStack || in instanceof Item || in instanceof Block)
                        ingredients.addIngredient(in, 1);
                }
            }

            for (Entry<Aspect, Integer> entry : recipe_aspects.aspects.entrySet()) {
                ingredients.addIngredient(ASPECT_MAP.get(entry.getKey()), entry.getValue());
            }

            if (output instanceof ItemStack)
                addConversion((ItemStack) output, ingredients.getMap());
            else
                addConversion(1, output, ingredients.getMap());
        }
    }

    private class ArcaneMapper extends ThaumcraftMapper {
        public ArcaneMapper() {
            super("Arcane");
        }

        @Override
        public void setup() {
            for (IArcaneRecipe recipe : ARCANE_RECIPES) {
                addRecipe(recipe.getRecipeOutput(), recipe.getIngredients(), recipe.getCrystals());
            }

            ARCANE_RECIPES.clear();
        }
    }

    private class CrucibleMapper extends ThaumcraftMapper {
        public CrucibleMapper() {
            super("Crucible");
        }

        @Override
        public void setup() {
            for (CrucibleRecipe recipe : CRUCIBLE_RECIPES) {
                addRecipe(recipe.getRecipeOutput(), recipe.getCatalyst(), recipe.getAspects());
            }

            CRUCIBLE_RECIPES.clear();
        }
    }

    private class InfusionMapper extends ThaumcraftMapper {
        public InfusionMapper() {
            super("Infusion");
        }

        @Override
        public void setup() {
            for (InfusionRecipe recipe : INFUSION_RECIPES) {
                addRecipe(recipe.getRecipeOutput(), recipe.getRecipeInput(), recipe.getAspects());
            }

            INFUSION_RECIPES.clear();
        }
    }
}
