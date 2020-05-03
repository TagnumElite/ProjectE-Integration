package com.tagnumelite.projecteintegration.plugins.tech;

import blusunrize.immersiveengineering.api.crafting.*;
import blusunrize.immersiveengineering.common.IEContent;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.internal.sized.SizedObject;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PEIPlugin("immersiveengineering")
public class PluginImmersiveEngineering extends APEIPlugin {
    public PluginImmersiveEngineering(String modid, Configuration config) {
        super(modid, config);
    }

    public static Object convertIngredientStack(IngredientStack stack) {
        if (stack.stack != null && !stack.stack.isEmpty()) {
            ItemStack item = stack.stack.copy();
            item.setCount(stack.inputSize);
            return item;
        }

        if (stack.stackList != null && stack.stackList.size() > 0)
            return new SizedObject<>(stack.inputSize, stack.stackList);

        if (stack.oreName != null)
            return new SizedObject<>(stack.inputSize, stack.oreName);

        if (stack.fluid != null) {
            FluidStack fluid = stack.fluid.copy();
            fluid.amount = stack.inputSize;
            return fluid;
        }

        PEIApi.LOG.warn("Failed to convert ingredient stack: {}", stack);
        return null;
    }

    @Override
    public void setup() {
        addMapper(new BlastFurnaceMapper());
        addMapper(new CokeOvenMapper());
        addMapper(new CrusherMapper());
        addMapper(new EngineerWorkbenchMapper());
        addMapper(new FermenterMapper());
        addMapper(new KilnMapper());
        addMapper(new MetalPressMapper());
        addMapper(new MixerMapper());
        addMapper(new RefineryMapper());
        addMapper(new SqueezerMapper());
    }

    private static class KilnMapper extends PEIMapper {
        public KilnMapper() {
            super("Kiln");
        }

        @Override
        public void setup() {
            for (AlloyRecipe recipe : AlloyRecipe.recipeList) {
                addRecipe(recipe.output, convertIngredientStack(recipe.input0), convertIngredientStack(recipe.input1));
            }
        }
    }

    private abstract static class MultiBlockRecipeMapper extends PEIMapper {
        public MultiBlockRecipeMapper(String name) {
            super(name);
        }

        protected void addRecipe(MultiblockRecipe recipe) {
            List<Object> item_inputs = recipe.getItemInputs().stream().map(PluginImmersiveEngineering::convertIngredientStack).collect(Collectors.toList());

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
            CrusherRecipe.recipeList.forEach(this::addRecipe);
        }
    }

    private static class EngineerWorkbenchMapper extends MultiBlockRecipeMapper {
        public EngineerWorkbenchMapper() {
            super("Engineer Workbench");
        }

        @Override
        public void setup() {
            BlueprintCraftingRecipe.recipeList.values().forEach(this::addRecipe);
        }
    }

    private static class MetalPressMapper extends MultiBlockRecipeMapper {
        public MetalPressMapper() {
            super("Metal Press");
        }

        @Override
        public void setup() {
            MetalPressRecipe.recipeList.values().forEach(this::addRecipe);
        }
    }

    private static class FermenterMapper extends MultiBlockRecipeMapper {
        public FermenterMapper() {
            super("Fermenter");
        }

        @Override
        public void setup() {
            FermenterRecipe.recipeList.forEach(this::addRecipe);
        }
    }

    private static class MixerMapper extends MultiBlockRecipeMapper {
        public MixerMapper() {
            super("Mixer");
        }

        @Override
        public void setup() {
            MixerRecipe.recipeList.forEach(this::addRecipe);
        }
    }

    private static class RefineryMapper extends MultiBlockRecipeMapper {
        public RefineryMapper() {
            super("Refinery");
        }

        @Override
        public void setup() {
            RefineryRecipe.recipeList.forEach(this::addRecipe);
        }
    }

    private static class SqueezerMapper extends MultiBlockRecipeMapper {
        public SqueezerMapper() {
            super("Squeezer");
        }

        @Override
        public void setup() {
            SqueezerRecipe.recipeList.forEach(this::addRecipe);
        }
    }

    private class BlastFurnaceMapper extends PEIMapper {
        private final boolean ignore_slag;

        public BlastFurnaceMapper() {
            super("Blast Furnace");
            ignore_slag = config.getBoolean(ConfigHelper.getConfigName(name + " ignore_slag"), category, true,
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

    private class CokeOvenMapper extends PEIMapper {
        private final boolean ingore_creosote;

        public CokeOvenMapper() {
            super("Coke Oven");
            ingore_creosote = config.getBoolean(ConfigHelper.getConfigName(name + " ignore_creosote"), category, true,
                "Should the EMC be split up with the creosote in the Coke Oven");
        }

        @Override
        public void setup() {
            for (CokeOvenRecipe recipe : CokeOvenRecipe.recipeList) {
                Object input = recipe.input;
                if (input instanceof List) input = PEIApi.getList((List<?>) input);

                if (recipe.creosoteOutput > 0 && !ingore_creosote) {
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
}
