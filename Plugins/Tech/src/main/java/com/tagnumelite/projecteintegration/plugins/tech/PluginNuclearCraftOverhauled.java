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

package com.tagnumelite.projecteintegration.plugins.tech;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import moze_intel.projecte.emc.IngredientMap;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.IRecipe;
import nc.recipe.NCRecipes;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PEIPlugin("nuclearcraft")
@OnlyIf(versionStartsWith = "2o.")
public class PluginNuclearCraftOverhauled extends APEIPlugin {
    public PluginNuclearCraftOverhauled(String modid, Configuration config) {
        super(modid, config);
    }

    @Override
    public void setup() {
        ArrayList<AbstractRecipeHandler<? extends IRecipe>> handlers = new ArrayList<>();

        Collections.addAll(handlers, NCRecipes.manufactory, NCRecipes.separator, NCRecipes.decay_hastener, NCRecipes.fuel_reprocessor, NCRecipes.alloy_furnace, NCRecipes.infuser, NCRecipes.melter, NCRecipes.supercooler, NCRecipes.electrolyzer, NCRecipes.assembler, NCRecipes.ingot_former, NCRecipes.pressurizer, NCRecipes.chemical_reactor, NCRecipes.salt_mixer, NCRecipes.crystallizer, NCRecipes.enricher, NCRecipes.extractor, NCRecipes.centrifuge, NCRecipes.rock_crusher, NCRecipes.collector, NCRecipes.decay_generator, NCRecipes.fission_moderator, NCRecipes.fission_reflector, NCRecipes.fission_irradiator, NCRecipes.pebble_fission, NCRecipes.solid_fission, NCRecipes.fission_heating, NCRecipes.salt_fission, NCRecipes.fusion, NCRecipes.coolant_heater, NCRecipes.heat_exchanger, NCRecipes.condenser, NCRecipes.turbine, NCRecipes.radiation_scrubber, NCRecipes.radiation_block_mutation, NCRecipes.radiation_block_purification);

        for (AbstractRecipeHandler<? extends IRecipe> handler : handlers) {
            addMapper(new AbstractRecipeMapper(handler));
        }
    }

    private static class AbstractRecipeMapper extends PEIMapper {
        private AbstractRecipeHandler<? extends IRecipe> handler;

        public AbstractRecipeMapper(AbstractRecipeHandler<? extends IRecipe> handler) {
            super(handler.getRecipeName());
            this.handler = handler;
        }

        private Object getObjectFromItemIngredient(IItemIngredient item) {
            Object obj = new Object();
            for (ItemStack input : item.getInputStackList()) {
                addConversion(1, obj, ImmutableMap.of(input, input.getCount()));
            }
            return obj;
        }

        private Object getObjectFromFluidIngredient(IFluidIngredient fluid) {
            Object obj = new Object();
            for (FluidStack input : fluid.getInputStackList()) {
                addConversion(1, obj, ImmutableMap.of(input, input.amount));
            }
            return obj;
        }

        @Override
        public void setup() {
            for (IRecipe recipe : handler.getRecipeList()) {

                List<IItemIngredient> item_inputs = recipe.getItemIngredients();
                List<IFluidIngredient> fluid_inputs = recipe.getFluidIngredients();

                List<IItemIngredient> item_outputs = recipe.getItemProducts();
                List<IFluidIngredient> fluid_outputs = recipe.getFluidProducts();

                if ((item_inputs.size() <= 0 && fluid_inputs.size() <= 0)
                    || (item_outputs.size() <= 0 && fluid_outputs.size() <= 0)) {
                    PEIntegration.LOG.warn("Invalid Recipe from `{}`", handler.getRecipeName());
                    continue;
                }

                IngredientMap<Object> ingredients = new IngredientMap<>();

                for (IItemIngredient input : item_inputs) {
                    ingredients.addIngredient(getObjectFromItemIngredient(input), input.getMaxStackSize(0));
                }

                for (IFluidIngredient input : fluid_inputs) {
                    ingredients.addIngredient(getObjectFromFluidIngredient(input), input.getMaxStackSize(0));
                }

                ArrayList<Object> output = new ArrayList<>();
                item_outputs.forEach(item -> {
                    PEIApi.LOG.debug("Item Input: {}; Output {};", item.getInputStackList(), item.getOutputStackList());
                    output.addAll(item.getOutputStackList());
                });
                fluid_outputs.forEach(fluid -> {
                    PEIApi.LOG.debug("Fluid Input: {}; Output {};", fluid.getInputStackList(),
                        fluid.getOutputStackList());
                    output.addAll(fluid.getInputStackList());
                });

                addConversion(output, ingredients.getMap());
            }
        }
    }
}
