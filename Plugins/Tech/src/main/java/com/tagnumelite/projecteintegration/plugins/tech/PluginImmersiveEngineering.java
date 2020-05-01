package com.tagnumelite.projecteintegration.plugins.tech;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.common.IEContent;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedIngredient;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
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

    public static SizedIngredient toSizedIng(IngredientStack stack) {
        return new SizedIngredient(stack.inputSize, stack.toRecipeIngredient());
    }

    private class BlastFurnaceMapper extends PEIMapper {
        private final boolean ignore_slag;

        public BlastFurnaceMapper() {
            super("Blast Furnace");
            ignore_slag = config.getBoolean(ConfigHelper.getConfigName(name+" ignore_slag"), category, true,
                "Should the EMC be split up with the slag in the Blast Furnace");
        }

        @Override
        public void setup() {
            for (BlastFurnaceRecipe recipe : BlastFurnaceRecipe.recipeList) {
                Object input = recipe.input;
                if (input instanceof List)
                    input = PEIApi.getList((List<?>) input);
                if (ignore_slag) {
                    addRecipe(recipe.output, input);
                } else {
                    ArrayList<Object> outputs = new ArrayList<>(2);
                    outputs.add(recipe.output);
                    outputs.add(recipe.slag);
                    addRecipe(outputs, input);
                }
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
                Object input = recipe.input;
                if (input instanceof List) input = PEIApi.getList((List<?>) input);

                if (recipe.creosoteOutput > 0) {
                    ArrayList<Object> outputs = new ArrayList<>();
                    outputs.add(recipe.output);
                    outputs.add(new FluidStack(IEContent.fluidCreosote, recipe.creosoteOutput));
                    addRecipe(outputs, input);
                } else {
                    addRecipe(recipe.output, input);
                }
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
                addRecipe(recipe.output, toSizedIng(recipe.input0), toSizedIng(recipe.input1));
            }
        }
    }

    private abstract static class MultiBlockRecipeMapper extends PEIMapper {
        public MultiBlockRecipeMapper(String name) {
            super(name);
        }

        protected void addRecipe(MultiblockRecipe recipe) {
            List<SizedIngredient> item_inputs = recipe.getItemInputs().stream().map(PluginImmersiveEngineering::toSizedIng).collect(Collectors.toList());

            ArrayList<Object> outputs = new ArrayList<>();
            if (recipe.getItemOutputs() != null) outputs.addAll(recipe.getItemOutputs());
            if (recipe.getFluidOutputs() != null) outputs.addAll(recipe.getFluidOutputs());

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
