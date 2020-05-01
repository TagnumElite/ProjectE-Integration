package com.tagnumelite.projecteintegration.plugins.tech;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.common.IEContent;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedIngredient;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PEIPlugin("immersiveengineering")
public class PluginImmersiveEngineering extends APEIPlugin {
    public PluginImmersiveEngineering(String modid, Configuration config) {
        super(modid, config);
    }

    @Override
    public void setup() {
        addMapper(new BlastFurnaceMapper());
        addMapper(new CokeOvenMapper());
        addMapper(new CrusherMapper());
        addMapper(new EngineerWorkbenchMapper());
        addMapper(new KilnMapper());
        addMapper(new MetalPressMapper());
    }

    private static class BlastFurnaceMapper extends PEIMapper {
        public BlastFurnaceMapper() {
            super("Blast Furnace");
        }

        @Override
        public void setup() {
            for (BlastFurnaceRecipe recipe : BlastFurnaceRecipe.recipeList) {
                ItemStack output = recipe.output;
                if (output == null || output.isEmpty())
                    continue;

                Object input = recipe.input;
                if (input == null)
                    continue;

                if (input instanceof List)
                    input = PEIApi.getList((List<?>) input);
                else if (!(input instanceof ItemStack) && !(input instanceof String))
                    continue;

                addConversion(output, ImmutableMap.of(input, 1));

                ItemStack slag = recipe.slag;
                if (slag == null || slag.isEmpty())
                    continue;

                addConversion(slag, ImmutableMap.of(input, 1));
            }
        }
    }

    private static class CokeOvenMapper extends PEIMapper {
        public CokeOvenMapper() {
            super("Coke Oven");
        }

        @Override
        public void setup() {
            for (CokeOvenRecipe recipe : CokeOvenRecipe.recipeList) {
                ItemStack output = recipe.output;
                if (output == null || output.isEmpty())
                    continue;

                Object input = recipe.input;
                if (input == null) {
                    continue;
                } else if (input instanceof ItemStack) {
                    if (((ItemStack) input).isEmpty())
                        continue;
                } else if (input instanceof String || input instanceof Item || input instanceof Block) {
                    //DO NOTHING!
                } else if (input instanceof Ingredient) {
                    input = PEIApi.getIngredient((Ingredient) input);
                } else if (input instanceof List) {
                    input = PEIApi.getList((List<?>) input);
                } else {
                    PEIApi.LOG.debug("Coke Oven Mapper: Unknown Input: {}, ({})", input, ClassUtils.getPackageCanonicalName(input.getClass()));
                    continue;
                }

                if (recipe.creosoteOutput > 0)
                    addRecipe(recipe.creosoteOutput, IEContent.fluidCreosote, ImmutableMap.of(input, 1));

                PEIApi.LOG.debug("Coke Oven Input: {}", ClassUtils.getPackageCanonicalName(input.getClass()));

                addConversion(output, ImmutableMap.of(input, 1));
            }
        }
    }

    private static class KilnMapper extends PEIMapper {
        public KilnMapper() {
            super("Kiln");
        }

        @Override
        public void setup() {
            for (AlloyRecipe recipe : AlloyRecipe.recipeList) {
                ItemStack output = recipe.output;
                if (output == null || output.isEmpty())
                    continue;

                IngredientStack input1 = recipe.input0;
                if (input1 == null || input1.inputSize <= 0)
                    continue;

                IngredientStack input2 = recipe.input1;
                if (input2 == null || input2.inputSize <= 0)
                    continue;

                //addRecipe(output.copy(), input1.) TODO: Output for kiln mapper
            }
        }
    }

    private abstract static class MultiBlockRecipeMapper extends PEIMapper {
        public MultiBlockRecipeMapper(String name) {
            super(name);
        }

        protected void addRecipe(MultiblockRecipe recipe) {
            List<SizedIngredient> item_inputs = recipe.getItemInputs().stream().map(r -> new SizedIngredient(r.inputSize, r.toRecipeIngredient())).collect(Collectors.toList());

            ArrayList<Object> outputs = new ArrayList<>(recipe.getItemOutputs());
            outputs.addAll(recipe.getFluidOutputs());

            addRecipe(outputs, item_inputs, recipe.getFluidInputs());
        }
    }

    private static class CrusherMapper extends MultiBlockRecipeMapper {
        public CrusherMapper() {
            super("Crusher");
        }

        @Override
        public void setup() {
            for (CrusherRecipe recipe : CrusherRecipe.recipeList) {
                addRecipe(recipe);
            }
        }
    }

    private static class EngineerWorkbenchMapper extends MultiBlockRecipeMapper {
        public EngineerWorkbenchMapper() {
            super("Engineer Workbench");
        }

        @Override
        public void setup() {
            for (BlueprintCraftingRecipe recipe : BlueprintCraftingRecipe.recipeList.values()) {
                addRecipe(recipe);
            }
        }
    }

    private static class MetalPressMapper extends MultiBlockRecipeMapper {
        public MetalPressMapper() {
            super("Metal Press");
        }

        @Override
        public void setup() {
            for (MetalPressRecipe recipe : MetalPressRecipe.recipeList.values()) {
                addRecipe(recipe);
            }
        }
    }
}
