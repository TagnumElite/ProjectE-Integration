/*
 * Copyright (c) 2019-2026 TagnumElite
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

import com.denfop.IUItem;
import com.denfop.api.Recipes;
import com.denfop.api.recipe.*;
import com.denfop.blocks.BlockClassicOre;
import com.denfop.blocks.BlockSpaceCobbleStone;
import com.denfop.blocks.BlockSpaceCobbleStone1;
import com.denfop.items.ItemCraftingElements;
import com.denfop.items.resource.ItemDust;
import com.denfop.items.resource.ItemIngots;
import com.denfop.items.resource.ItemNuclearResource;
import com.denfop.recipe.IInputItemStack;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ACustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.CustomRecipeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndustrialUpgradeAddon {
    public static final String MODID = "industrialupgrade";

    public static String NAME(String name) {
        return "IndustrialUpgrade" + name + "Mapper";
    }

    @CustomRecipeMapper(MODID)
    public static class IndustrialUpgradeMapper extends ACustomRecipeMapper<BaseMachineRecipe> {
        @Override
        public String getName() {
            return "IndustrialUpgradeMapper";
        }

        @Override
        public List<BaseMachineRecipe> getRecipes() {
            List<BaseMachineRecipe> recipes = new ArrayList<>();
            for (String manager : Recipes.recipes.getMap_recipe_managers()) {
                recipes.addAll(Recipes.recipes.getRecipeList(manager));
            }
            return recipes;
        }

        @Override
        public NSSOutput getOutput(BaseMachineRecipe recipe) {
            NSSOutput.Builder builder = getOutputBuilder();

            for (ItemStack stack : recipe.getOutput().items) {
                builder.addItem(stack);
            }

            return builder.build();
        }

        @Override
        public NSSInput getInput(BaseMachineRecipe recipe) {
            NSSInput.Builder builder = getInputBuilder();
            IInput input = recipe.input;

            for (IInputItemStack itemInput : input.getInputs()) {
                if (itemInput.hasTag()) builder.addIngredient(itemInput.getAmount(), Ingredient.of(itemInput.getTag()));
                else
                    builder.addIngredient(itemInput.getAmount(), Ingredient.of(itemInput.getInputs().toArray(new ItemStack[0])));
            }

            if (input.hasFluids()) builder.addFluid(input.getFluid());

            if (input.getFluidInputs() != null) {
                for (FluidStack fluidInput : input.getFluidInputs()) {
                    builder.addFluid(fluidInput);
                }
            }

            return builder.build();
        }

        @Override
        // Not Used!
        protected List<Ingredient> getIngredients(BaseMachineRecipe recipe) {
            return List.of();
        }

        @Override
        // Not Used
        protected ItemStack getResult(BaseMachineRecipe recipe) {
            return null;
        }
    }

    @CustomRecipeMapper(MODID)
    public static class IndustrialUpgradeFluidMapper extends ACustomRecipeMapper<BaseFluidMachineRecipe> {
        @Override
        public String getName() {
            return "IndustrialUpgradeFluidMapper";
        }

        @Override
        public List<BaseFluidMachineRecipe> getRecipes() {
            return Recipes.recipes.getRecipeFluid().map_recipes_fluid
                    .values().stream().flatMap(Collection::stream).toList();
        }

        @Override
        public NSSOutput getOutput(BaseFluidMachineRecipe recipe) {
            NSSOutput.Builder builder = getOutputBuilder();

            recipe.getOutput_fluid().forEach(builder::addFluid);

            if (recipe.getOutput() != null) {
                recipe.getOutput().items.forEach(builder::addItem);
            }

            return builder.build();
        }

        @Override
        public NSSInput getInput(BaseFluidMachineRecipe recipe) {
            NSSInput.Builder builder = getInputBuilder();
            IInputFluid input = recipe.input;

            input.getInputs().forEach(builder::addFluid);

            IInputItemStack itemInput = input.getStack();
            if (input.getStack() != null) {
                if (itemInput.hasTag()) builder.addIngredient(itemInput.getAmount(), Ingredient.of(itemInput.getTag()));
                else builder.addIngredient(itemInput.getAmount(), Ingredient.of(itemInput.getInputs().toArray(new ItemStack[0])));
            }

            return builder.build();
        }

        @Override
        // Not Used!
        protected List<Ingredient> getIngredients(BaseFluidMachineRecipe recipe) {
            return List.of();
        }

        @Override
        // Not Used
        protected ItemStack getResult(BaseFluidMachineRecipe recipe) {
            return null;
        }
    }

    @ConversionProvider(MODID)
    public static class IndustrialUpgradeConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            // Crafting Element
            // - 290: Sticky Resin
            // - 448: Boron
            builder.before(IUItem.crafting_elements.getStack(ItemCraftingElements.Types.crafting_290_element), 8)
                    .before(IUItem.crafting_elements.getStack(ItemCraftingElements.Types.crafting_448_element), 8)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.mikhail_ingot), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.yttrium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.vanadium_ingot), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.germanium_ingot), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.barium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.bismuth), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.gadolinium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.tantalum), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.polonium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.cadmium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.meteoric_iron), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.bloodstone), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.draconid), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.mithril), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.orichalcum), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.adamantium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.zirconium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.strontium), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.arsenic), 256)
                    .before(IUItem.iuingot.getStack(ItemIngots.ItemIngotsTypes.molybdenum), 256)
                    .before(IUItem.iudust.getStack(ItemDust.ItemDustTypes.calcium_sulfate), 16)
                    .before(IUItem.iudust.getStack(ItemDust.ItemDustTypes.calcium_fluoride), 16)
                    .before(IUItem.nuclear_res.getStack(ItemNuclearResource.Types.americium_dust), 4192)
                    .before(IUItem.nuclear_res.getStack(ItemNuclearResource.Types.neptunium_dust), 4192)
                    .before(IUItem.nuclear_res.getStack(ItemNuclearResource.Types.unprocessed_radioactive_uranium), 4192)
                    .before(IUItem.nuclear_res.getStack(ItemNuclearResource.Types.thorium_shard), 4192 / 9)
                    .before(IUItem.nuclear_res.getStack(ItemNuclearResource.Types.curium_dust), 4192)
                    .conversion(IUItem.diamondDust).ingredient(Items.DIAMOND).end()
                    .conversion(IUItem.coalDust).ingredient(Items.COAL).end();

            int space_cobble_emc = 16;
            for (BlockSpaceCobbleStone.Type cobble : BlockSpaceCobbleStone.Type.values()) {
                builder.before(IUItem.space_cobblestone.getItemStack(cobble), space_cobble_emc);
            }

            for (BlockSpaceCobbleStone1.Type cobble : BlockSpaceCobbleStone1.Type.values()) {
                builder.before(IUItem.space_cobblestone1.getItemStack(cobble), space_cobble_emc);
            }
        }
    }
}
