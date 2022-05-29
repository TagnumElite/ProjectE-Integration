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

import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;

import java.util.ArrayList;
import java.util.List;

public class TinkersConstructAddon {
    public static final String MODID = "tconstruct";

    static String NAME(String name) {
        return "TinkersConstruct" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class TCAlloyingMapper extends ARecipeTypeMapper<AlloyRecipe> {
        @Override
        public String getName() {
            return NAME("Alloying");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypes.ALLOYING;
        }

        @Override
        public NSSInput getInput(AlloyRecipe recipe) {
            return convertFluidIngredients(recipe.getDisplayInputs());
        }

        @Override
        public NSSOutput getOutput(AlloyRecipe recipe) {
            return new NSSOutput(recipe.getOutput());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class TCCastingMapper extends ARecipeTypeMapper<ICastingRecipe> {
        @Override
        public String getName() {
            return NAME("Casting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypes.CASTING_BASIN || recipeType == RecipeTypes.CASTING_TABLE;
        }

        @Override
        public boolean convertRecipe(ICastingRecipe recipe) {
            if (recipe instanceof ItemCastingRecipe) {
                return convertItemCastingRecipe((ItemCastingRecipe) recipe);
            }
            // TODO: Support MaterialCastingRecipe, CompositeCastingRecipe
            PEIntegration.LOGGER.warn("Unsupported Tinkers Casting recipe ({}), currently only ItemCastingRecipe.", recipe);
            return false;
        }

        private boolean convertItemCastingRecipe(ItemCastingRecipe recipe) {
            ItemStack output = recipe.getResultItem();
            if (output.isEmpty()) {
                PEIntegration.debugLog("Recipe ({}) contains no outputs: {}", recipeID, recipe);
                return false;
            }

            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();

            if (recipe.hasCast() && recipe.isConsumed() && !convertIngredient(recipe.getCast(), ingredientMap, fakeGroupMap)) {
                return addConversionsAndReturn(fakeGroupMap, false);
            }

            if (!convertFluidIngredient(recipe.getFluids(), ingredientMap, fakeGroupMap)) {
                return addConversionsAndReturn(fakeGroupMap, false);
            }

            mapper.addConversion(output.getCount(), NSSItem.createItem(output), ingredientMap.getMap());
            return addConversionsAndReturn(fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class TCMeltingMapper extends ARecipeTypeMapper<IMeltingRecipe> {
        @Override
        public String getName() {
            return NAME("Melting");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RecipeTypes.MELTING;
        }

        @Override
        public NSSOutput getOutput(IMeltingRecipe recipe) {
            if (recipe instanceof MeltingRecipe) {
                return mapOutput(((MeltingRecipe) recipe).getDisplayOutput());
            } else {
                return NSSOutput.EMPTY;
            }
        }
    }

    /* Part Builder not currently supported
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class TCPartMapper extends ARecipeTypeMapper<IPartBuilderRecipe> {
        @Override
        public String getName() {
            return NAME("Part");
        }

        @Override
        public String getDescription() {
            return "null";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType ==recipeTypes.PART_BUILDER;
        }

        @Override
        protected List<Ingredient> getIngredients(IPartBuilderRecipe recipe) {
            if (recipe instanceof ItemPartRecipe) {
                return Collections.singletonList(Ingredient.of(MaterialItemList.getItems(((ItemPartRecipe) recipe).getMaterialId()).stream()));
            }
            return super.getIngredients(recipe);
        }

        // Tinkers handles their recipes in a special way.
        @Override
        public boolean convertRecipe(IPartBuilderRecipe recipe) {
            if (recipe instanceof PartRecipe) {
                return handlePartRecipe((PartRecipe) recipe);
            }
            return super.convertRecipe(recipe);
        }

        private boolean handlePartRecipe(PartRecipe mainRecipe) {
            int convertedCount = 0;
            for (ItemPartRecipe recipe : mainRecipe.getRecipes()) {
                if (!convertRecipe(recipe)) {
                    PEIntegration.LOGGER.warn("Tinkers PartRecipe failed to be converted! Skipping...");
                } else {
                    convertedCount += 1;
                }
            }
            PEIntegration.debugLog("Parsed {} {} Recipes", convertedCount, mainRecipe.getId());
            return true;
        }
    }
     */

    /* Materials are not currently supported
    @NBTProcessor(requiredMods = MODID, priority = 1)
    public static class TCNBTMaterialProcessor extends ANBTProcessor {
        static IEMCProxy emcProxy = ProjectEAPI.getEMCProxy();

        @Override
        public String getName() {
            return "TinkersMaterialProcessor";
        }

        @Override
        public String getDescription() {
            return "Increases the EMC value with the material the item is constructed out of.";
        }

        @Override
        public long recalculateEMC(@Nonnull ItemInfo itemInfo, long emc) throws ArithmeticException {
            final ItemStack stack = itemInfo.createStack();
            final CompoundNBT nbt = itemInfo.getNBT();

            List<IMaterial> materials = new ArrayList<>(1);
            ToolStack toolStack = null;

            if (stack.getItem() instanceof IMaterialItem) {
                PEIntegration.debugLog("IMaterialItem found: {} - {}", stack, nbt);
                 materials.add(IMaterialItem.getMaterialFromStack(stack));
            } else if (ToolStack.hasMaterials(stack)) {
                PEIntegration.debugLog("Finding Tool Stack: {} - {}", stack, nbt);
                toolStack = ToolStack.copyFrom(stack);
                materials = toolStack.getMaterialsList();
            }

            if (materials.isEmpty()) {
                return emc;
            }

            long ret = emc;

            for (IMaterial material : materials) {
                // TODO: Calculate how much extra the material will add.
                material.getIdentifier(); // TODO: Use the ID to get emc value
                if (toolStack != null) {
                    toolStack.getCurrentDurability();
                    toolStack.getDamage();
                    toolStack.isBroken();
                    toolStack.isUnbreakable();
                }
                ret = Math.addExact(ret, 1000000);
            }

            return ret;
        }
    }
     */

    @ConversionProvider(MODID)
    public static class TCDataGenerator extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for Tinkers Construct")
                    .before(ingotTag("cobalt"), 2048 * 2);
        }
    }
}
