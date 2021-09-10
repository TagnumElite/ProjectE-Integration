package com.tagnumelite.projecteintegration.compat;

import com.blakebr0.extendedcrafting.api.crafting.ICompressorRecipe;
import com.blakebr0.extendedcrafting.api.crafting.RecipeTypes;
import com.blakebr0.extendedcrafting.config.ModConfigs;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.mappers.recipe.BaseRecipeTypeMapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtendedCraftingMapper {
    /**
     * Because ProjectE BasicRecipeTypeMapper$addIngredient is private, I have to remake-ish it here.
     */
    @RecipeTypeMapper(requiredMods = "extendedcrafting", priority = 1)
    public static class ECCompressorMapper implements IRecipeTypeMapper {
        @Override
        public String getName() {
            return "ExtendedCraftingCompressorMapper";
        }

        @Override
        public String getDescription() {
            return "Maps the Extended Crafting compressor recipes";
        }

        @Override
        public boolean canHandle(IRecipeType<?> iRecipeType) {
            return iRecipeType == RecipeTypes.COMPRESSOR && ModConfigs.ENABLE_COMPRESSOR.get();
        }

        /** This is a dumbed down version of https://github.com/sinkillerj/ProjectE/blob/10fdd41f26d1dca4183e1ebcb74c990bf2d17a96/src/main/java/moze_intel/projecte/emc/mappers/recipe/BaseRecipeTypeMapper.java#L36 */
        @Override
        public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> iRecipe, INSSFakeGroupManager fakeGroupManager) {
            ICompressorRecipe recipe = (ICompressorRecipe) iRecipe;
            ItemStack recipeOutput = recipe.getResultItem();
            if (recipeOutput.isEmpty()) {
                return false;
            }
            Ingredient inputItem = recipe.getIngredients().get(0);
            if (inputItem.isEmpty()) {
                return false;
            }

            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
            ItemStack[] matches = inputItem.getItems();

            if (matches.length == 1) {
                addIngredient(ingredientMap, matches[0].copy(), recipe.getInputCount());
            } else if (matches.length > 0) {
                Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemStack match : matches) {
                    if (!match.isEmpty()) {
                        //Validate it is not an empty stack in case mods do weird things in custom ingredients
                        rawNSSMatches.add(NSSItem.createItem(match));
                        stacks.add(match);
                    }
                }
                int count = stacks.size();
                if (count == 1) {
                    addIngredient(ingredientMap, stacks.get(0).copy(), recipe.getInputCount());
                } else if (count > 1) {
                    //Handle this ingredient as the representation of all the stacks it supports
                    Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                    NormalizedSimpleStack dummy = group.getA();
                    ingredientMap.addIngredient(dummy, recipe.getInputCount());
                    if (group.getB()) {
                        //Only lookup the matching stacks for the group with conversion if we don't already have
                        // a group created for this dummy ingredient
                        // Note: We soft ignore cases where it fails/there are no matching group ingredients
                        // as then our fake ingredient will never actually have an emc value assigned with it
                        // so the recipe won't either
                        List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                        for (ItemStack stack : stacks) {
                            IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
                            addIngredient(groupIngredientMap, stack.copy(), 1);
                            groupIngredientMaps.add(groupIngredientMap);
                        }
                        dummyGroupInfos.add(new Tuple<>(dummy, groupIngredientMaps));
                    }
                }
            }
            mapper.addConversion(recipeOutput.getCount(), NSSItem.createItem(recipeOutput), ingredientMap.getMap());
            return addConversionsAndReturn(mapper, dummyGroupInfos);
        }

        /**
         * This method can be used as a helper method to return a specific value and add any existing group conversions. It is important that we add any valid group
         * conversions that we have, regardless of whether the recipe as a whole is valid, because we only create one instance of our group's NSS representation so even if
         * parts of the recipe are not valid, the conversion may be valid and exist in another recipe.
         */
        private boolean addConversionsAndReturn(IMappingCollector<NormalizedSimpleStack, Long> mapper,
                                                List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos) {
            //If we have any conversions make sure to add them even if we are returning early
            for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
                for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
                    mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
                }
            }
            return true;
        }

        private void addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, int inputCount) {
            ingredientMap.addIngredient(NSSItem.createItem(stack), inputCount);
        }

        private boolean isTagException(Exception e) {
            return e instanceof IllegalStateException && e.getMessage().matches("Tag \\S*:\\S* used before it was bound");
        }
    }

    @RecipeTypeMapper(requiredMods = "extendedcrafting", priority = 1)
    public static class ECBasicMapper extends BaseRecipeTypeMapper {
        @Override
        public String getName() {
            return "ExtendedCraftingRecipeTypes";
        }

        @Override
        public String getDescription() {
            return "Maps the different extended crafting recipe types";
        }

        @Override
        public boolean canHandle(IRecipeType<?> recipeType) {
            boolean ENDER_CRAFTING = recipeType == RecipeTypes.ENDER_CRAFTER && ModConfigs.ENABLE_ENDER_CRAFTER.get();
            boolean TABLE_CRAFTING = recipeType == RecipeTypes.TABLE && ModConfigs.ENABLE_TABLES.get();
            boolean COMBINATION_CRAFTING = recipeType == RecipeTypes.COMBINATION && ModConfigs.ENABLE_CRAFTING_CORE.get();
            return ENDER_CRAFTING || TABLE_CRAFTING || COMBINATION_CRAFTING;
        }
    }
}
