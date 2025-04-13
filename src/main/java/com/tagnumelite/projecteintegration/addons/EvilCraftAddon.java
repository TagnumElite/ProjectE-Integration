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
import com.tagnumelite.projecteintegration.api.recipe.nss.NSSInput;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.cyclops.evilcraft.RegistryEntries;
import org.cyclops.evilcraft.core.recipe.type.RecipeBloodInfuser;
import org.cyclops.evilcraft.core.recipe.type.RecipeEnvironmentalAccumulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EvilCraftAddon {
    public static final String MODID = "evilcraft";

    public static String NAME(String name) {
        return "EvilCraft" + name + "Mapper";
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECBloodInfuserMapper extends ARecipeTypeMapper<RecipeBloodInfuser> {
        @Override
        public String getName() {
            return NAME("BloodInfuser");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RegistryEntries.RECIPETYPE_BLOOD_INFUSER.get();
        }

        @Override
        public NSSInput getInput(RecipeBloodInfuser recipe) {
            Object2IntMap<NormalizedSimpleStack> ingMap = new Object2IntOpenHashMap<>();
            List<Tuple<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            convertIngredient(recipe.getInputIngredient().get(), ingMap, fakeGroupMap);
            // We divide the value by ten otherwise blood will add too much EMC
            ingMap.mergeInt(NSSFluid.createFluid(recipe.getInputFluid().get()), recipe.getInputFluid().get().getAmount() / 10, Constants.INT_SUM);
            return new NSSInput(ingMap, fakeGroupMap, true);
        }
    }

    @RecipeTypeMapper(requiredMods = MODID, priority = 1)
    public static class ECEnvironmentalAccumulatorMapper extends ARecipeTypeMapper<RecipeEnvironmentalAccumulator> {
        @Override
        public String getName() {
            return NAME("EnvironmentalAccumulator");
        }

        @Override
        public boolean canHandle(RecipeType<?> recipeType) {
            return recipeType == RegistryEntries.RECIPETYPE_ENVIRONMENTAL_ACCUMULATOR.get();
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeEnvironmentalAccumulator recipe) {
            return Collections.singletonList(recipe.getInputIngredient());
        }
    }

    @ConversionProvider(MODID)
    public static class ECConversionProvider extends AConversionProvider {
        private static final Item ITEM_VENGEANCE_ESSENCE;
        private static final Item ITEM_ENDER_TEAR;

        static {
            ITEM_VENGEANCE_ESSENCE = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, "vengeance_essence"));
            ITEM_ENDER_TEAR = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, "ender_tear"));
        }

        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for EvilCraft")
                    .before(RegistryEntries.FLUID_BLOOD.get(), 1)
                    .before(RegistryEntries.ITEM_POISON_SAC.get(), 16)
                    .before(RegistryEntries.ITEM_DARK_GEM.get(), 64)
                    .before(RegistryEntries.ITEM_WEREWOLF_BONE.get(), 144)
                    .before(ITEM_VENGEANCE_ESSENCE, 512)
                    .before(ITEM_ENDER_TEAR, 1024)
                    .conversion(RegistryEntries.BLOCK_HARDENED_BLOOD.get()).ingredient(RegistryEntries.FLUID_BLOOD.get(), 1000).end()
                    .conversion(RegistryEntries.ITEM_DARK_GEM_CRUSHED.get()).ingredient(RegistryEntries.ITEM_DARK_GEM.get()).end();
        }
    }
}
