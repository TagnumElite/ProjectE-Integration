/*
 * Copyright (c) 2019-2021 TagnumElite
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

package com.tagnumelite.projecteintegration.compat;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.crusher.CrushingRecipe;
import com.simibubi.create.content.contraptions.components.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.content.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.CompactingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.APEIRecipeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Can you see this below, maybe not gut.

public class CreateMappers {
    public static final String MODID = "create";

    static String NAME(String name) {
        return "Create"+name+"Mapper";
    }

    static String DESC(String name) {
        return "Create "+name+" Mapper";
    }

    private abstract static class CreateProcessingRecipeMapper<R extends ProcessingRecipe<?>> extends APEIRecipeMapper<R> {
        @Override
        protected NSSInput getInput(R recipe) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            NonNullList<FluidIngredient> fluidIngredients = recipe.getFluidIngredients();
            if (ingredients.isEmpty() && fluidIngredients.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no inputs: (Ingredients: {}; Fluids: {})", recipeID, ingredients, fluidIngredients);
                return null;
            }

            // A 'Map' of NormalizedSimpleStack and List<IngredientMap>
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            for (Ingredient ingredient : ingredients) {
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
        protected NSSOutput getOutput(R recipe) {
            List<Object> outputs = new ArrayList<>();
            List<ItemStack> results = recipe.getRollableResults().stream().filter(pO -> pO.getChance() >= 1.0f).map(ProcessingOutput::getStack).collect(Collectors.toList());
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
        public String getDescription() {
            return DESC("BASIN");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.BASIN.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCompactingMapper extends CreateProcessingRecipeMapper<CompactingRecipe> {
        @Override
        public String getName() {
            return NAME("Compacting");
        }

        @Override
        public String getDescription() {
            return DESC("Compacting");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.COMPACTING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCrushingMapper extends CreateProcessingRecipeMapper<CrushingRecipe> {
        @Override
        public String getName() {
            return NAME("Crushing");
        }

        @Override
        public String getDescription() {
            return DESC("Crushing");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.CRUSHING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateCuttingMapper extends CreateProcessingRecipeMapper<CuttingRecipe> {
        @Override
        public String getName() {
            return NAME("Cutting");
        }

        @Override
        public String getDescription() {
            return DESC("Cutting");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.CUTTING.getType();
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
    //    public boolean canHandle(IRecipeType<?> iRecipeType) {
    //        return iRecipeType == AllRecipeTypes.CONVERSION.getType();
    //    }
    //}

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMillingMapper extends CreateProcessingRecipeMapper<MillingRecipe> {
        @Override
        public String getName() {
            return NAME("Milling");
        }

        @Override
        public String getDescription() {
            return DESC("Milling");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.MILLING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMixingMapper extends CreateProcessingRecipeMapper<MixingRecipe> {
        @Override
        public String getName() {
            return NAME("CUTTING");
        }

        @Override
        public String getDescription() {
            return DESC("CUTTING");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.CUTTING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreatePressingMapper extends CreateProcessingRecipeMapper<PressingRecipe> {
        @Override
        public String getName() {
            return NAME("Pressing");
        }

        @Override
        public String getDescription() {
            return DESC("Pressing");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.PRESSING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateSplashingMapper extends CreateProcessingRecipeMapper<SplashingRecipe> {
        @Override
        public String getName() {
            return NAME("Splashing");
        }

        @Override
        public String getDescription() {
            return DESC("Splashing");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.SPLASHING.getType();
        }
    }
    /* Maybe not filling and emptying recipes just yet.
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateFillingMapper extends CreateProcessingRecipeMapper<FillingRecipe> {
        @Override
        public String getName() {
            return NAME("Filling");
        }

        @Override
        public String getDescription() {
            return DESC("Filling");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.FILLING.getType();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateEmptyingMapper extends CreateProcessingRecipeMapper<EmptyingRecipe> {
        @Override
        public String getName() {
            return NAME("Emptying");
        }

        @Override
        public String getDescription() {
            return DESC("Emptying");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.EMPTYING.getType();
        }
    }
     */
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateDeployerApplicationMapper extends CreateProcessingRecipeMapper<DeployerApplicationRecipe> {
        @Override
        public String getName() {
            return NAME("DeployerApplication");
        }

        @Override
        public String getDescription() {
            return DESC("Deployer Application");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.DEPLOYING.getType();
        }
    }

    /* TODO: BELOW
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateMechanicalCraftingMapper extends CreateProcessingRecipeMapper<MechanicalCraftingRecipe> {
        @Override
        public String getName() {
            return NAME("MechanicalCrafting");
        }

        @Override
        public String getDescription() {
            return DESC("Mechanical Crafting");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.CUTTING.getType();
        }
    }
     */
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class CreateSandPaperPolishingMapper extends CreateProcessingRecipeMapper<SandPaperPolishingRecipe> {
        @Override
        public String getName() {
            return NAME("SandpaperPolishing");
        }

        @Override
        public String getDescription() {
            return DESC("Sandpaper Polishing");
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == AllRecipeTypes.SANDPAPER_POLISHING.getType();
        }
    }
}
