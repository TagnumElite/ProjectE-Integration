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
import com.tagnumelite.projecteintegration.api.recipe.ACustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.CustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.*;

public class Utils {
    private static final Type CUSTOM_RECIPE_MAPPER_TYPE = Type.getType(CustomRecipeMapper.class);

    public static Map<? extends ACustomRecipeMapper, String> getCustomRecipeMappers() {
        ModList modList = ModList.get();
        Map<ACustomRecipeMapper, String> recipeTypeMappers = new HashMap<>();
        for (ModFileScanData scanData : modList.getAllScanData()) {
            for (ModFileScanData.AnnotationData data : scanData.getAnnotations()) {
                if (CUSTOM_RECIPE_MAPPER_TYPE.equals(data.getAnnotationType()) && checkRequiredMod(data)) {
                    ACustomRecipeMapper mapper = createOrGetInstance(data.getMemberName(), ACustomRecipeMapper.class);
                    if (mapper != null) {
                        recipeTypeMappers.put(mapper, getAnnotationData(data, "value"));
                        PEIntegration.LOGGER.info("Instantiated custom recipe mapper: {}", mapper.getName());
                    }
                }
            }
        }
        return recipeTypeMappers;
    }

    public static <T> T createOrGetInstance(String className, Class<T> baseClass) {
        try {
            Class<? extends T> subClass = Class.forName(className).asSubclass(baseClass);
            return subClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
            PEIntegration.LOGGER.error("Failed to load: {}", className, e);
        }
        return null;
    }

    public static boolean checkRequiredMod(ModFileScanData.AnnotationData data) {
        return checkRequiredMod(data, "value");
    }

    public static boolean checkRequiredMod(ModFileScanData.AnnotationData data, String key) {
        String modId = getAnnotationData(data, key);
        if (modId != null && !ModList.get().isLoaded(modId)) {
            PEIntegration.debugLog("Skipped checking class {}, as its required mod ({}) is not loaded.", data.getMemberName(), modId);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationData(ModFileScanData.AnnotationData data, String key) {
        Map<String, Object> annotationData = data.getAnnotationData();
        if (annotationData.containsKey(key)) {
            try {
                return (T) annotationData.get(key);
            } catch (ClassCastException e) {
                PEIntegration.LOGGER.fatal("Annotation Data {}:{} was casted to an invalid class", key, annotationData.get(key), e);
            }
        }
        return null;
    }

    public static boolean convertIngredient(int amount, Ingredient ingredient, IngredientMap<NormalizedSimpleStack> ingredientMap, List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap, INSSFakeGroupManager fakeGroupManager, String recipeID) {
        ItemStack[] matches = getMatchingStacks(ingredient, recipeID);
        if (matches == null) {
            return false;
        } else if (matches.length == 1) {
            //Handle this ingredient as a direct representation of the stack it represents
            return !addIngredient(ingredientMap, getStack(matches[0], amount), recipeID);
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
                return !addIngredient(ingredientMap, getStack(stacks.get(0), amount), recipeID);
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
                    for (ItemStack stack : stacks) {
                        IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                        if (addIngredient(groupIngredientMap, stack.copy(), recipeID)) {
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

    // Borrowed from ProjectE with a few modifications
    // https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/src/main/java/moze_intel/projecte/emc/mappers/recipe/BaseRecipeTypeMapper.java#L148-L185
    public static boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, String recipeID) {
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

    public static boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, FluidStack stack) {
        ingredientMap.addIngredient(NSSFluid.createFluid(stack), stack.getAmount());
        return true;
    }

    // Also from ProjectE, a dependency for the above code:
    // https://github.com/sinkillerj/ProjectE/blob/mc1.16.x/src/main/java/moze_intel/projecte/emc/mappers/recipe/BaseRecipeTypeMapper.java#L187-L189
    public static boolean isTagException(Exception e) {
        return e instanceof IllegalStateException && e.getMessage().matches("Tag \\S*:\\S* used before it was bound");
    }

    public static NSSOutput mapOutputs(IMappingCollector<NormalizedSimpleStack, Long> mapper, INSSFakeGroupManager fakeGroupManager, String recipeID, Object... allOutputs) {
        List<Object> outputs = Arrays.asList(allOutputs);
        if (allOutputs.length == 1 && allOutputs[0] instanceof Collection) {
            outputs = new ArrayList<>((Collection<?>) allOutputs[0]);
        }

        // Assume output stacks will be the size length as outputs
        Map<NormalizedSimpleStack, Integer> outputStacks = new HashMap<>(outputs.size());

        int totalOutputs = 0;
        for (Object output : outputs) {
            if (output == null) continue;
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

    /**
     * @param ingredient
     * @param recipeID
     * @return
     */
    public static ItemStack[] getMatchingStacks(Ingredient ingredient, String recipeID) {
        try {
            return ingredient.getItems();
        } catch (Exception e) {
            PEIntegration.LOGGER.fatal("Failed to map recipe ({}). Ingredient ({}) failed to get matching stacks", recipeID, ingredient.getClass().getName(), e);
            return null;
        }
    }

    /**
     * @param item
     * @param amount
     * @return
     */
    public static ItemStack getStack(ItemStack item, int amount) {
        if (amount > 0) {
            return new ItemStack(item.getItem(), amount);
        }
        return item.copy();
    }

    /**
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy) {
        return getDummyMap(dummy, 1);
    }

    /**
     * @param dummy
     * @return
     */
    public static Map<NormalizedSimpleStack, Integer> getDummyMap(NormalizedSimpleStack dummy, int amount) {
        IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
        ingredientMap.addIngredient(dummy, amount);
        return ingredientMap.getMap();
    }
}
