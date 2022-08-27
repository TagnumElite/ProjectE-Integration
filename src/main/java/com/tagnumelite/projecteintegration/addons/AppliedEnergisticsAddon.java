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

import appeng.api.ids.AEItemIds;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipe;
import com.tagnumelite.projecteintegration.api.conversion.AConversionProvider;
import com.tagnumelite.projecteintegration.api.conversion.ConversionProvider;
import com.tagnumelite.projecteintegration.api.recipe.ARecipeTypeMapper;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.List;

public class AppliedEnergisticsAddon {
    public static final String MODID = "ae2"; // Great idea, I'll change my modid to pei. Perfect right.

    public static String NAME(String name) {
        return "AppliedEnergistics" + name + "Mapper";
    }
/*
    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AEGrinderMapper extends BaseRecipeTypeMapper {
        @Override
        public String getName() {
            return NAME("Grinder");
        }

        @Override
        public String getDescription() {
            return "Recipe mapper for Applied Energistics grinder. NOTE: Optional outputs are ignored";
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == GrinderRecipe.TYPE;
        }
    }
 */

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class AEInscriberMapper extends ARecipeTypeMapper<InscriberRecipe> {
        @Override
        public String getName() {
            return NAME("Inscriber");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == InscriberRecipe.TYPE;
        }

        @Override
        protected List<Ingredient> getIngredients(InscriberRecipe recipe) {
            // Whoops, forgot that inscribing exists.
            if (recipe.getProcessType() == InscriberProcessType.INSCRIBE) {
                return Collections.singletonList(recipe.getMiddleInput());
            }
            return super.getIngredients(recipe);
        }
    }

    @ConversionProvider(MODID)
    public static class AEConversionProvider extends AConversionProvider {
        static final RegistryObject<Item> CERTUS_QUARTZ_CRYSTAL = RegistryObject.create(AEItemIds.CERTUS_QUARTZ_CRYSTAL, ForgeRegistries.ITEMS);
        static final RegistryObject<Item> CERTUS_QUARTZ_CRYSTAL_CHARGED = RegistryObject.create(AEItemIds.CERTUS_QUARTZ_CRYSTAL_CHARGED, ForgeRegistries.ITEMS);
        static final RegistryObject<Item> FLUIX_DUST = RegistryObject.create(AEItemIds.FLUIX_DUST, ForgeRegistries.ITEMS);
        static final RegistryObject<Item> FLUIX_CRYSTAL = RegistryObject.create(AEItemIds.FLUIX_CRYSTAL, ForgeRegistries.ITEMS);
        //public static final Item NETHER_QUARTZ_SEED = RegistryObject.create(AEItemIds.NETHER_QUARTZ_SEED, ForgeRegistries.ITEMS);
        static final RegistryObject<Item> FLUIX_CRYSTAL_SEED = RegistryObject.create(AEItemIds.FLUIX_CRYSTAL_SEED, ForgeRegistries.ITEMS);
        static final RegistryObject<Item> CERTUS_CRYSTAL_SEED = RegistryObject.create(AEItemIds.CERTUS_CRYSTAL_SEED, ForgeRegistries.ITEMS);

        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Set defaults conversions for Applied Energistics")
                    .before(CERTUS_QUARTZ_CRYSTAL.get(), 256)
                    .conversion(CERTUS_QUARTZ_CRYSTAL_CHARGED.get()).ingredient(CERTUS_QUARTZ_CRYSTAL.get()).end()
                    .conversion(FLUIX_DUST.get(), 2).ingredient(CERTUS_QUARTZ_CRYSTAL_CHARGED.get()).ingredient(Tags.Items.DUSTS_REDSTONE).ingredient(Items.QUARTZ).end()
                    //.conversion(Items.QUARTZ).ingredient(NETHER_QUARTZ_SEED).end()
                    .conversion(FLUIX_CRYSTAL.get()).ingredient(FLUIX_CRYSTAL_SEED.get()).end()
                    .conversion(CERTUS_QUARTZ_CRYSTAL.get()).ingredient(CERTUS_CRYSTAL_SEED.get()).end();
        }
    }
}
