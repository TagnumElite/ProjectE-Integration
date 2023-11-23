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
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

// TODO: Can you see this below, maybe not gut.
public class CreateAddon {
    public static final String MODID = "create";

    static String NAME(String name) {
        return "Create" + name + "Mapper";
    }

    private abstract static class CreateProcessingRecipeMapper<R extends ProcessingRecipe<?>> extends ARecipeTypeMapper<R> {
        @Override
        public NSSInput getInput(R recipe) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            NonNullList<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
            if (ingredients.isEmpty() && fluidIngredients.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: (Ingredients: {}; Fluids: {})", recipeID, ingredients, fluidIngredients);
                return null;
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                if (recipe instanceof ItemApplicationRecipe iaRecipe && iaRecipe.shouldKeepHeldItem() && i == 1)
                    continue; // Skip ItemApplicationRecipe's held item if it is not consumed.
                if (!convertIngredient(ingredient, ingredientMap, fakeGroupMap)) {
                    return new NSSInput(ingredientMap, fakeGroupMap, false);
                }
            }

            for (FluidIngredient fluidIngredient : fluidIngredients) {
                final int amount = fluidIngredient.getRequiredAmount();
                List<FluidStack> matches = fluidIngredient.getMatchingFluidStacks();
                if (matches.isEmpty()) {
                    //PEIntegration.LOGGER.warn("");
                    continue;
                }

                if (matches.size() == 1) {
                    ingredientMap.addIngredient(NSSFluid.createFluid(matches.get(0)), amount);
                } else {
                    Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                    List<FluidStack> stacks = new ArrayList<>();

                    for (FluidStack match : matches) {
                        //Validate it is not an empty stack in case mods do weird things in custom ingredients
                        if (!match.isEmpty()) {
                            rawNSSMatches.add(NSSFluid.createFluid(match));
                            stacks.add(match);
                        }
                    }

                    int count = stacks.size();
                    if (count == 1) {
                        ingredientMap.addIngredient(NSSFluid.createFluid(stacks.get(0)), amount);
                    } else if (count > 1) {
                        //Handle this ingredient as the representation of all the stacks it supports
                        Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                        NormalizedSimpleStack dummy = group.getA();
                        ingredientMap.addIngredient(dummy, Math.max(amount, 1));
                        if (group.getB()) {
                            //Only lookup the matching stacks for the group with conversion if we don't already have
                            // a group created for this dummy ingredient
                            // Note: We soft ignore cases where it fails/there are no matching group ingredients
                            // as then our fake ingredient will never actually have an emc value assigned with it
                            // so the recipe won't either
                            List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                            for (FluidStack stack : stacks) {
                                IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                                groupIngredientMap.addIngredient(NSSFluid.createFluid(stack), 1);
                                groupIngredientMaps.add(groupIngredientMap);
                            }
                            fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                        }
                    }
                }
            }

            return new NSSInput(ingredientMap, fakeGroupMap, true);
        }

        @Override
        public NSSOutput getOutput(R recipe) {
            List<Object> outputs = new ArrayList<>();
            List<ItemStack> results = recipe.getRollableResults().stream().filter(pO -> pO.getChance() >= 1.0f).map(ProcessingOutput::getStack).toList();
            outputs.addAll(results);
            outputs.addAll(recipe.getFluidResults());

            if (outputs.size() == 0) return NSSOutput.EMPTY;
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
            return Collections.singletonList(recipe.getIngredient());
        }

        @Override
        public NSSOutput getOutput(SequencedAssemblyRecipe recipe) {
            // TODO: Add support for mapOutput multiple items with 100% chance
            if (recipe.getOutputChance() != 1f) return null;

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
