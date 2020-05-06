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
package com.tagnumelite.projecteintegration.plugins.library;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.RecipeTranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PEIPlugin("reborncore")
public class PluginRebornCore extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new RebornCoreMapper());
    }

    private class RebornCoreMapper extends PEIMapper {
        private final Map<String, Boolean> RECIPE_CFG_MAP = new HashMap<>();

        public RebornCoreMapper() {
            super("Reborn Core");
        }

        @Override
        public void setup() {
            for (IBaseRecipeType recipe : RecipeHandler.recipeList) {
                String recipe_name = recipe.getRecipeName();
                if (RECIPE_CFG_MAP.containsKey(recipe_name)) {
                    if (!RECIPE_CFG_MAP.get(recipe_name))
                        continue;
                } else {
                    RECIPE_CFG_MAP.put(recipe_name, config.getBoolean(ConfigHelper.getConversionName(recipe_name),
                        category, true, "Enable conversions for machine " + recipe_name));
                }

                List<Object> outputs = new ArrayList<>(recipe.getOutputs());
                if (outputs == null || outputs.isEmpty())
                    continue;

                List<Object> inputs = recipe.getInputs();
                if (inputs == null || inputs.isEmpty())
                    continue;

                IngredientMap<Object> ingredients = new IngredientMap<Object>();

                inputs.forEach(input -> {
                    ItemStack item = RecipeTranslator.getStackFromObject(input);
                    ingredients.addIngredient(item, item.getCount());
                });

                Map<Object, Integer> map = ingredients.getMap();
                if (map == null || map.isEmpty())
                    continue;

                addConversion(outputs, map);
				/*
				outputs.forEach(output -> {
					if (output != null && !output.isEmpty())
						addConversion(output, map);
				});*/
            }

            RECIPE_CFG_MAP.clear();
        }
    }
}
