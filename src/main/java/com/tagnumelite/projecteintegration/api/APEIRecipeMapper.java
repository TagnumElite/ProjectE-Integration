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

package com.tagnumelite.projecteintegration.api;

import com.tagnumelite.projecteintegration.PEIntegration;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * An abstract class used for converting {@link IRecipe}s to EMC conversion maps.
 *
 * This function has a few helper variables: {@link #recipeID}, {@link #mapper} and {@link #fakeGroupManager}
 * 
 * This class follows closely to {@link moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper} with added
 * function for special recipes with {@link ItemStack} and/or {@link FluidStack} outputs.
 * 
 * To overwrite output, override method {@link #getOutput(IRecipe)}
 * to overwrite input, override method {@link #getIngredients(IRecipe)}
 *
 * For an example on how to handle recipes with multiple outputs, look at {@link com.tagnumelite.projecteintegration.compat.ImmersiveEngineeringMappers}
 */
public abstract class APEIRecipeMapper<R extends IRecipe<?>> implements IRecipeTypeMapper {
    protected ResourceLocation recipeID;
    protected IMappingCollector<NormalizedSimpleStack, Long> mapper;
    protected INSSFakeGroupManager fakeGroupManager;

    /**
     * Convert the recipe into an EMC map
     *
     * @param recipe The recipe to be converted into an EMC Map
     * @return A boolean value denoting whether the recipe was handled successfully or not.
     */
    protected boolean convertRecipe(R recipe) {
        NSSOutput output = getOutput(recipe);
        if (output == null) {
            PEIntegration.debugLog("Recipe ({}) contains no outputs: {}", recipeID, recipe.getResultItem());
            return false;
        }

        NSSInput input = getInput(recipe);
        if (input == null || !input.successful) {
            return addConversionsAndReturn(input != null ? input.fakeGroupMap : null, true);
        }

        mapper.addConversion(output.amount, output.nss, input.getMap());
        return addConversionsAndReturn(input.fakeGroupMap, true);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> iRecipe, INSSFakeGroupManager fakeGroupManager) {
        this.mapper = mapper;
        this.fakeGroupManager = fakeGroupManager;
        recipeID = iRecipe.getId();
        try {
            return convertRecipe((R) iRecipe);
        } catch (ClassCastException e) {
            PEIntegration.LOGGER.fatal("RecipeMapper ({}) is unable to handle recipe ({}), expected ({})",
                    getClass().getName(), iRecipe.getClass().getName(),
                    ((Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName());
            return false;
        }
    }

    /**
     *
     * @param recipe
     * @return
     */
    protected NSSOutput getOutput(R recipe) {
        ItemStack output = recipe.getResultItem();
        if (output.isEmpty()) return null;
        return new NSSOutput(output);
    }

    /**
     *
     * @param recipe
     * @return
     */
    protected List<Ingredient> getIngredients(R recipe) {
        return recipe.getIngredients();
    }

    /**
     *
     * @param recipe
     * @return
     */
    protected NSSInput getInput(R recipe) {
        List<Ingredient> ingredients = getIngredients(recipe);
        if (ingredients == null || ingredients.isEmpty()) {
            PEIntegration.debugLog("Recipe ({}) contains no inputs: {}", recipeID, ingredients);
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
        return new NSSInput(ingredientMap, fakeGroupMap, true);
    }

    /**
     *
     * @param item
     * @param amount
     * @return
     */
    protected static ItemStack getStack(ItemStack item, int amount) {
        if (amount > 0) {
            return new ItemStack(item.getItem(), amount);
        }
        return item.copy();
    }

    /**
     *
     * @param ingredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertIngredient(Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                         List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        return convertIngredient(-1, ingredient, ingredientMap, fakeGroupMap);
    }

    /**
     *
     * @param amount
     * @param ingredient
     * @param ingredientMap
     * @param fakeGroupMap
     * @return
     */
    protected boolean convertIngredient(int amount, Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap,
                                                List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap) {
        ItemStack[] matches = getMatchingStacks(ingredient);
        if (matches == null) {
            return false;
        } else if (matches.length == 1) {
            //Handle this ingredient as a direct representation of the stack it represents
            return !addIngredient(ingredientMap, getStack(matches[0], amount));
        } else if (matches.length > 0) {
            Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack match : matches) {
                //Validate it is not an empty stack in case mods do weird things in custom ingredients
                if (!match.isEmpty()) {
                    rawNSSMatches.add(NSSItem.createItem(match));
                    stacks.add(match);
                }
            }

            int count = stacks.size();
            if (count == 1) {
                return !addIngredient(ingredientMap, getStack(stacks.get(0), amount));
            } else if (count > 1) {
                //Handle this ingredient as the representation of all the stacks it supports
                Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                NormalizedSimpleStack dummy = group.getA();
                ingredientMap.addIngredient(dummy, amount);
                if (group.getB()) {
                    //Only lookup the matching stacks for the group with conversion if we don't already have
                    // a group created for this dummy ingredient
                    // Note: We soft ignore cases where it fails/there are no matching group ingredients
                    // as then our fake ingredient will never actually have an emc value assigned with it
                    // so the recipe won't either
                    List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                    for (ItemStack stack : stacks) {
                        IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                        if (addIngredient(groupIngredientMap, stack.copy())) {
                            return false;
                        }
                        groupIngredientMaps.add(groupIngredientMap);
                    }
                    fakeGroupMap.add(new Tuple<>(dummy, groupIngredientMaps));
                }
            }
        }
        return true;
    }

    /**
     *
     * @param ingredient
     * @return
     */
    protected ItemStack[] getMatchingStacks(Ingredient ingredient) {
        try {
            return ingredient.getItems();
        } catch (Exception e) {
            PEIntegration.LOGGER.fatal("Failed to map recipe ({}). Ingredient ({}) failed to get matching stacks", recipeID, ingredient.getClass().getName(), e);
            return null;
        }
    }

    /**
     *
     * @param outputs
     * @return
     */
    protected NSSOutput mapOutputs(Object... outputs) {
        // Assume output stacks will be the size length as outputs
        Map<NormalizedSimpleStack, Integer> outputStacks = new HashMap<>(outputs.length);

        int totalOutputs = 0;
        for (Object output : outputs) {
            if (output instanceof ItemStack) {
                ItemStack item = (ItemStack) output;
                outputStacks.put(NSSItem.createItem(item), item.getCount());
                totalOutputs += item.getCount();
            } else if (output instanceof FluidStack) {
                FluidStack fluid = (FluidStack) output;
                outputStacks.put(NSSFluid.createFluid(fluid), fluid.getAmount());
                totalOutputs += fluid.getAmount();
            } else {
                PEIntegration.LOGGER.warn("Recipe ({}) has unsupported output: {}. Skipping...", recipeID, output);
            }
        }

        NormalizedSimpleStack dummy = fakeGroupManager.getOrCreateFakeGroup(outputStacks.keySet()).getA();

        for (Map.Entry<NormalizedSimpleStack, Integer> entry : outputStacks.entrySet()) {
            mapper.addConversion(entry.getValue(), entry.getKey(), getDummyMap(dummy, entry.getValue()));
        }

        return new NSSOutput(totalOutputs, dummy);
    }

    // Borrowed from ProjectE with a few modifications
    // https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/src/main/java/moze_intel/projecte/emc/mappers/recipe/BaseRecipeTypeMapper.java#L148-L185
    protected boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, ItemStack stack) {
        Item item = stack.getItem();
        boolean hasContainerItem = false;
        try {
            //Note: We include the hasContainerItem check in the try catch, as if a mod is handling tags incorrectly
            // there is a chance their hasContainerItem is checking something about tags, and
            hasContainerItem = item.hasContainerItem(stack);
            if (hasContainerItem) {
                //If this item has a container for the stack, we remove the cost of the container itself
                ingredientMap.addIngredient(NSSItem.createItem(item.getContainerItem(stack)), -1);
            }
        } catch (Exception e) {
            ResourceLocation itemName = item.getRegistryName();
            if (hasContainerItem) {
                if (isTagException(e)) {
                    PEIntegration.LOGGER.fatal("Error mapping recipe {}. Item: {} reported that it has a container item, "
                            + "but errors when trying to get the container item due to not properly deserializing and handling tags. "
                            + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
                } else {
                    PEIntegration.LOGGER.fatal("Error mapping recipe {}. Item: {} reported that it has a container item, "
                            + "but errors when trying to get the container item based on the stack in the recipe. "
                            + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
                }
            } else if (isTagException(e)) {
                PEIntegration.LOGGER.fatal("Error mapping recipe {}. Item: {} crashed when checking if the stack has a container item, "
                                + "due to not properly deserializing and handling tags. Please report this to {}.", recipeID, itemName,
                        itemName.getNamespace(), e);
            } else {
                PEIntegration.LOGGER.fatal("Error mapping recipe {}. Item: {} crashed when checking if the stack in the recipe has a container item. "
                        + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
            }
            //If something failed because the recipe errored, return that we did handle it so that we don't try to handle it later
            // as there is a 99% chance it will just fail again anyways
            return true;
        }
        ingredientMap.addIngredient(NSSItem.createItem(stack), stack.getCount());
        return false;
    }

    // Also from ProjectE, a dependency for the above code:
    // https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/src/main/java/moze_intel/projecte/emc/mappers/recipe/BaseRecipeTypeMapper.java#L187-L189
    protected boolean isTagException(Exception e) {
        return e instanceof IllegalStateException && e.getMessage().matches("Tag \\S*:\\S* used before it was bound");
    }

    //TODO: CHANGE THE BELOW CODE SOON!
    /**
     * This method can be used as a helper method to return a specific value and add any existing group conversions. It is important that we add any valid group
     * conversions that we have, regardless of whether the recipe as a whole is valid, because we only create one instance of our group's NSS representation so even if
     * parts of the recipe are not valid, the conversion may be valid and exist in another recipe.
     */
    protected boolean addConversionsAndReturn(List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos, boolean returnValue) {
        //If we have any conversions make sure to add them even if we are returning early
        if (dummyGroupInfos != null) {
            for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
                for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
                    mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
                }
            }
        }
        return returnValue;
    }

    /**
     *
     * @param stacks
     * @return
     */
    protected Tuple<NormalizedSimpleStack, Boolean> getFakeGroup(NormalizedSimpleStack... stacks) {
        return fakeGroupManager.getOrCreateFakeGroup(new HashSet<>(Arrays.asList(stacks)));
    }

    /**
     *
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy) {
        return getDummyMap(dummy, 1);
    }

    /**
     *
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy, int amount) {
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
        ingredientMap.addIngredient(dummy, amount);
        return ingredientMap.getMap();
    }

    /**
     * A 'data' class to hold both a {@link NormalizedSimpleStack} and an integer denoting the output item and amount.
     */
    public static class NSSOutput {
        public final NormalizedSimpleStack nss;
        public final int amount;

        /**
         * Create an NSSOutput with the specified amount and NSS
         * @param amount The amount that will be outputted
         * @param nss The item that will be outputted
         */
        public NSSOutput(int amount, NormalizedSimpleStack nss) {
            this.amount = amount;
            this.nss = nss;
        }

        /**
         * A helper constructor to create an NSSOutput from an {@link ItemStack}
         * @param item The {@link ItemStack} to be converted
         */
        public NSSOutput(ItemStack item) {
            this.amount = item.getCount();
            this.nss = NSSItem.createItem(item);
        }

        /**
         * A helper constructor to create an NSSOutput from an {@link FluidStack}
         * @param fluid The {@link FluidStack} to be converted
         */
        public NSSOutput(FluidStack fluid) {
            this.amount = fluid.getAmount();
            this.nss = NSSFluid.createFluid(fluid);
        }
    }

    /**
     *
     */
    public static class NSSInput {
        public final IngredientMap<NormalizedSimpleStack> ingredientMap;
        public final List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap;
        public final boolean successful;

        /**
         * @param ingredientMap
         * @param fakeGroupMap
         * @param successful
         */
        public NSSInput(IngredientMap<NormalizedSimpleStack> ingredientMap,
                        List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap,
                        boolean successful) {
            this.ingredientMap = ingredientMap;
            this.fakeGroupMap = fakeGroupMap;
            this.successful = successful;
        }

        /**
         *
         * @return
         */
        public Map<NormalizedSimpleStack, Integer> getMap() {
            return ingredientMap.getMap();
        }
    }
}
