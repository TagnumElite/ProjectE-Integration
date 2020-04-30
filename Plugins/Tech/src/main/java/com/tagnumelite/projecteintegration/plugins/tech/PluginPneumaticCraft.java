/*
 * Copyright (c) 2019 TagnumElite
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

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import me.desht.pneumaticcraft.api.recipe.IPressureChamberRecipe;
import me.desht.pneumaticcraft.api.recipe.IThermopneumaticProcessingPlantRecipe;
import me.desht.pneumaticcraft.api.recipe.ItemIngredient;
import me.desht.pneumaticcraft.common.recipes.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PEIPlugin("pneumaticcraft")
public class PluginPneumaticCraft extends APEIPlugin {
    private final boolean ignore_loss_rate;

    public PluginPneumaticCraft(String modid, Configuration config) {
        super(modid, config);
        ignore_loss_rate = config.getBoolean("ignore_explosion_loss_rate", ConfigHelper.getPluginCategory("pneumaticcraft"), false, "Ignore loss rate for explosion crafting");
    }

    @Override
    public void setup() {
        addMapper(new AmadronMapper());
        addMapper(new AssemblyMapper(AssemblyMapper.AssemblyType.DRILL));
        addMapper(new AssemblyMapper(AssemblyMapper.AssemblyType.LAZER));
        addMapper(new BasicThermopneumaticProcessingPlantMapper());
        addMapper(new ExplosionCraftingMapper());
        addMapper(new HeatFrameCoolingMapper());
        addMapper(new PressureChamberMapper());
        addMapper(new RefineryMapper());
    }

    private static class AmadronMapper extends PEIMapper {
        public AmadronMapper() {
            super("Amadron", "Runs only on static offers, ignores player made offers and villager trades.");
        }

        @Override
        public void setup() {
            for (AmadronOffer offer : AmadronOfferManager.getInstance().getStaticOffers()) {
                Object output = offer.getOutput();
                if (output instanceof ItemStack)
                    addRecipe((ItemStack) output, offer.getInput());
                else
                    addRecipe((FluidStack) output, offer.getInput());
            }
        }
    }

    private static class AssemblyMapper extends PEIMapper {
        private final AssemblyType assemblyType;

        public AssemblyMapper(AssemblyType assemblyType) {
            super("assembly_" + assemblyType.name());
            this.assemblyType = assemblyType;
        }

        @Override
        public void setup() {
            List<AssemblyRecipe> recipes;
            switch (this.assemblyType) {
                case DRILL:
                    recipes = AssemblyRecipe.drillRecipes;
                    break;
                case LAZER:
                    recipes = AssemblyRecipe.laserRecipes;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + this.assemblyType);
            }

            for (AssemblyRecipe recipe : recipes) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }

        private enum AssemblyType {
            DRILL,
            LAZER
        }
    }

    private static class BasicThermopneumaticProcessingPlantMapper extends PEIMapper {
        public BasicThermopneumaticProcessingPlantMapper() {
            super("Basic Thermopneumatic Processing Plant");
        }

        @Override
        public void setup() {
            for (IThermopneumaticProcessingPlantRecipe iRecipe : BasicThermopneumaticProcessingPlantRecipe.recipes) {
                if (iRecipe instanceof BasicThermopneumaticProcessingPlantRecipe) {
                    BasicThermopneumaticProcessingPlantRecipe recipe = (BasicThermopneumaticProcessingPlantRecipe) iRecipe;
                    addRecipe(recipe.getOutputLiquid(), recipe.getInputItem(), recipe.getInputLiquid());
                }
            }
        }
    }

    private static class HeatFrameCoolingMapper extends PEIMapper {
        public HeatFrameCoolingMapper() {
            super("Heat Frame Cooling");
        }

        @Override
        public void setup() {
            for (HeatFrameCoolingRecipe recipe : HeatFrameCoolingRecipe.recipes) {
                // TODO: Ask author to make the OreDict string publicly accessible
                addRecipe(recipe.output, recipe.input.getSingleStack());
            }
        }
    }

    private static class PressureChamberMapper extends PEIMapper {
        public PressureChamberMapper() {
            super("Pressure Chamber");
        }

        @Override
        public void setup() {
            for (IPressureChamberRecipe recipe : PressureChamberRecipe.recipes) {
                List<ItemStack> inputs = new ArrayList<>();
                for (ItemIngredient input : recipe.getInput()) {
                    inputs.add(input.getSingleStack());
                }

                addRecipe(new ArrayList<>(recipe.getResult()), inputs);
            }
        }
    }

    private static class RefineryMapper extends PEIMapper {
        public RefineryMapper() {
            super("Refinery");
        }

        @Override
        public void setup() {
            for (RefineryRecipe recipe : RefineryRecipe.recipes) {
                addRecipe(Collections.singletonList(recipe.outputs), recipe.input);
            }
        }
    }

    private class ExplosionCraftingMapper extends PEIMapper {
        public ExplosionCraftingMapper() {
            super("Explosion Crafting");
        }

        @Override
        public void setup() {
            for (ExplosionCraftingRecipe recipe : ExplosionCraftingRecipe.recipes) {
                Object input = recipe.getInput();
                if (recipe.getInput().isEmpty() && recipe.getOreDictKey() != null) input = recipe.getOreDictKey();

                if (ignore_loss_rate || recipe.getLossRate() <= 0) {
                    addRecipe(recipe.getOutput(), input);
                }
            }
        }
    }
}
