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

import com.tagnumelite.projecteintegration.api.internal.lists.InputList;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.IRecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PEIPlugin("reborncore")
public class PluginRebornCore extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new RebornCoreMapper());
    }

    private class RebornCoreMapper extends PEIMapper {
        public RebornCoreMapper() {
            super("Reborn Core");
        }

        @Override
        public void setup() {
            // Convert List<IBaseRecipeType> into map of IBaseRecipeType.getRecipeName and List<IBaseRecipeType>
            Map<String, List<IBaseRecipeType>> recipe_map = RecipeHandler.recipeList.stream()
                .collect(Collectors.groupingBy(IBaseRecipeType::getRecipeName));

            for (String name : recipe_map.keySet()) {
                if (config.getBoolean(ConfigHelper.getConversionName(name), category,
                    true, "Enable conversions for machine " + name)) {
                    for (IBaseRecipeType recipe : recipe_map.get(name)) {
                        List<Object> outputs = new ArrayList<>(recipe.getOutputs());
                        if (outputs.isEmpty())
                            continue;

                        List<Object> inputs = new ArrayList<>();
                        for (Object input : recipe.getInputs()) {
                            if (input instanceof IRecipeInput) {
                                inputs.add(new InputList<Object>(((IRecipeInput) input).getAllStacks()));
                            } else {
                                inputs.add(input);
                            }
                        }

                        addRecipe(outputs, inputs);
                    }
                }
            }
        }
    }
}
