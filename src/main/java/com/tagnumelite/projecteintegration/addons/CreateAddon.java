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

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateAddon {
    public static final String MODID = "create";

    static String NAME(String name) {
        return "Create" + name + "Mapper";
    }

    public abstract static class CreateProcessingRecipeMapper<R extends ProcessingRecipe<?, ?>> extends ARecipeTypeMapper<R> {
        @Override
        public NSSInput getInput(R recipe) {
            if (recipe.getIngredients().isEmpty() && recipe.getFluidIngredients().isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs", recipeID);
                return null;
            }

            NSSInput.Builder builder = getInputBuilder();
            recipe.getFluidIngredients().forEach(builder::addFluid);

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                if (recipe instanceof ItemApplicationRecipe iaRecipe && iaRecipe.shouldKeepHeldItem() && i == 1)
                    continue; // Skip ItemApplicationRecipe's held item if it is not consumed.
                builder.addIngredient(ingredient);
            }

            return builder.build();
        }

        @Override
        public NSSOutput getOutput(R recipe) {
            List<Object> outputs = new ArrayList<>();
            List<ItemStack> results = recipe.getRollableResults().stream().filter(pO -> pO.getChance() >= 1.0f)
                    .map(ProcessingOutput::getStack).toList();
            outputs.addAll(results);
            outputs.addAll(recipe.getFluidResults());

            if (outputs.isEmpty()) return NSSOutput.EMPTY;
            return mapOutputs(outputs.toArray());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateBasinMapper extends CreateProcessingRecipeMapper<BasinRecipe> {

        @Override
        public String getName() {
            return NAME("BASIN");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.BASIN.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCompactingMapper extends CreateProcessingRecipeMapper<CompactingRecipe> {
        @Override
        public String getName() {
            return NAME("Compacting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.COMPACTING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCrushingMapper extends CreateProcessingRecipeMapper<CrushingRecipe> {
        @Override
        public String getName() {
            return NAME("Crushing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.CRUSHING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCuttingMapper extends CreateProcessingRecipeMapper<CuttingRecipe> {
        @Override
        public String getName() {
            return NAME("Cutting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.CUTTING.getType();
        }
    }

    // TODO: BELOW
    //@RecipeTypeMapper(requiredMods = MODID, priority = 1)
    //public static class CreateConversionMapper extends CreateProcessingRecipeMapper<ConversionRecipe> {
    //    @Override
    //    public String getName() {
    //        return NAME("Conversion");
    //    }
    //
    //    @Override
    //    public String getDescription() {
    //        return DESC("Conversion");
    //    }
    //
    //    @Override
    //    public boolean canHandle(RecipeType<?>recipeType) {
    //        returnrecipeType == AllRecipeTypes.CONVERSION.getType();
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateHauntingMapper extends CreateProcessingRecipeMapper<HauntingRecipe> {
        @Override
        public String getName() {
            return NAME("Haunting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.HAUNTING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMillingMapper extends CreateProcessingRecipeMapper<MillingRecipe> {
        @Override
        public String getName() {
            return NAME("Milling");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.MILLING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMixingMapper extends CreateProcessingRecipeMapper<MixingRecipe> {
        @Override
        public String getName() {
            return NAME("CUTTING");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.MIXING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreatePressingMapper extends CreateProcessingRecipeMapper<PressingRecipe> {
        @Override
        public String getName() {
            return NAME("Pressing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.PRESSING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateSplashingMapper extends CreateProcessingRecipeMapper<SplashingRecipe> {
        @Override
        public String getName() {
            return NAME("Splashing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.SPLASHING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateDeployerApplicationMapper extends CreateProcessingRecipeMapper<DeployerApplicationRecipe> {
        @Override
        public String getName() {
            return NAME("DeployerApplication");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.DEPLOYING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMechanicalCraftingMapper extends ARecipeTypeMapper<MechanicalCraftingRecipe> {
        @Override
        public String getName() {
            return NAME("MechanicalCrafting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.MECHANICAL_CRAFTING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateItemApplicationMapper extends CreateProcessingRecipeMapper<ItemApplicationRecipe> {
        @Override
        public String getName() {
            return NAME("ItemApplication");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.ITEM_APPLICATION.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateSequencedAssemblyMapper extends ARecipeTypeMapper<SequencedAssemblyRecipe> {
        @Override
        public String getName() {
            return NAME("SequencedAssembly");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.SEQUENCED_ASSEMBLY.getType();
        }

        @Override
        protected List<Ingredient> getIngredients(SequencedAssemblyRecipe recipe) {
            final int loops = recipe.getLoops();
            final List<Ingredient> ingredients = new ArrayList<>(loops * recipe.getSequence().size());
            ingredients.add(recipe.getIngredient());

            int i = 0;
            for (SequencedRecipe<?> step : recipe.getSequence()) {
                final ProcessingRecipe<?, ?> stepRecipe = step.getRecipe();
                final List<Ingredient> stepIngredients = new ArrayList<>(stepRecipe.getIngredients());

                // TODO: use fluids from stepRecipe.getFluidIngredients();

                // We skip the first ingredient because it is the input from the previous step
                stepIngredients.removeFirst();
                // We ignore steps with one ingredient because it doesn't add anything else
                if (stepIngredients.isEmpty()) continue;

                if (loops == 1) {
                    ingredients.addAll(stepIngredients);
                } else {
                    for (Ingredient ingredient : stepIngredients) {
                        ingredients.addAll(Collections.nCopies(loops, ingredient));
                    }
                }

                i++;
            }

            return ingredients;
        }

        @Override
        public NSSOutput getOutput(SequencedAssemblyRecipe recipe) {
            // We include recipes with reduced output chance
            //if (recipe.getOutputChance() < 1f) return null;

            ItemStack output = recipe.getResultItem(registryAccess);
            if (output.isEmpty()) return null;
            return new NSSOutput(output);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateSandPaperPolishingMapper extends CreateProcessingRecipeMapper<SandPaperPolishingRecipe> {
        @Override
        public String getName() {
            return NAME("SandpaperPolishing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == AllRecipeTypes.SANDPAPER_POLISHING.getType();
        }
    }

    // We are ignoring Filling, Emptying recipes.
}
