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

import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSOutput;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import sirttas.elementalcraft.item.ECItems;
import sirttas.elementalcraft.property.ECProperties;
import sirttas.elementalcraft.recipe.ECRecipeTypes;
import sirttas.elementalcraft.recipe.PureInfusionRecipe;
import sirttas.elementalcraft.recipe.SpellCraftRecipe;
import sirttas.elementalcraft.recipe.instrument.CrystallizationRecipe;
import sirttas.elementalcraft.recipe.instrument.InscriptionRecipe;
import sirttas.elementalcraft.recipe.instrument.binding.AbstractBindingRecipe;
import sirttas.elementalcraft.recipe.instrument.infusion.IInfusionRecipe;
import sirttas.elementalcraft.recipe.instrument.io.grinding.IGrindingRecipe;
import sirttas.elementalcraft.recipe.instrument.io.sawing.SawingRecipe;

public class ElementalCraftAddon {
    public static final String MODID = "elementalcraft";

    public static String NAME(String name) {
        return "ElementalCraft" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECInfusionMapper extends ARecipeTypeMapper<IInfusionRecipe> {
        @Override
        public String getName() {
            return NAME("Infusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.INFUSION.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECBindingMapper extends ARecipeTypeMapper<AbstractBindingRecipe> {
        @Override
        public String getName() {
            return NAME("Binding");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.BINDING.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECCrystallizationMapper extends ARecipeTypeMapper<CrystallizationRecipe> {
        @Override
        public String getName() {
            return NAME("Crystallization");
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " NOTE: Skips all recipes with more than one output chance";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.CRYSTALLIZATION.get();
        }

        @Override
        public NSSOutput getOutput(CrystallizationRecipe recipe) {
            if (recipe.getOutputs().size() != 1) {
                return NSSOutput.EMPTY;
            }
            return new NSSOutput(recipe.getOutputs().get(0).getResult());
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECInscriptionMapper extends ARecipeTypeMapper<InscriptionRecipe> {
        @Override
        public String getName() {
            return NAME("Inscription");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.INSCRIPTION.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECGrindingMapper extends ARecipeTypeMapper<IGrindingRecipe> {
        @Override
        public String getName() {
            return NAME("Grinding");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.AIR_MILL_GRINDING.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECSawingMapper extends ARecipeTypeMapper<SawingRecipe> {
        @Override
        public String getName() {
            return NAME("Sawing");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.SAWING.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECPureInfusionMapper extends ARecipeTypeMapper<PureInfusionRecipe> {
        @Override
        public String getName() {
            return NAME("PureInfusion");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.PURE_INFUSION.get();
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECSpellCraftMapper extends ARecipeTypeMapper<SpellCraftRecipe> {
        @Override
        public String getName() {
            return NAME("SpellCraft");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == ECRecipeTypes.SPELL_CRAFT.get();
        }
    }

    @ConversionProvider(MODID)
    public static class ECConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for ElementalCraft")
                    .before(ECItems.INERT_CRYSTAL.get(), 8);
            gem(builder, ECItems.CRUDE_AIR_GEM.get(), ECItems.FINE_AIR_GEM.get(), ECItems.AIR_CRYSTAL.get(), ECItems.AIR_SHARD.get());
            gem(builder, ECItems.CRUDE_EARTH_GEM.get(), ECItems.FINE_EARTH_GEM.get(), ECItems.AIR_CRYSTAL.get(), ECItems.AIR_SHARD.get());
            gem(builder, ECItems.CRUDE_FIRE_GEM.get(), ECItems.FINE_FIRE_GEM.get(), ECItems.AIR_CRYSTAL.get(), ECItems.AIR_SHARD.get());
            gem(builder, ECItems.CRUDE_WATER_GEM.get(), ECItems.FINE_WATER_GEM.get(), ECItems.AIR_CRYSTAL.get(), ECItems.AIR_SHARD.get());
        }

        protected void gem(CustomConversionBuilder builder, Item crudeGem, Item fineGem, Item crystal, Item shard) {
            builder.conversion(crudeGem).ingredient(Items.DIAMOND).ingredient(crystal).ingredient(shard).end();
            builder.conversion(fineGem).ingredient(Items.DIAMOND).ingredient(crystal).ingredient(shard).end();
        }
    }
}
