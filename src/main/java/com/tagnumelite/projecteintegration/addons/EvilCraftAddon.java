/*
 * Copyright (c) 2019-2023 TagnumElite
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
import moze_intel.projecte.api.data.CustomConversionBuilder;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.util.Tuple;
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
            return recipeType == RegistryEntries.RECIPETYPE_BLOOD_INFUSER;
        }

        @Override
        public NSSInput getInput(RecipeBloodInfuser recipe) {
            IngredientMap<NormalizedSimpleStack> ingMap = new IngredientMap<>();
            List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();

            convertIngredient(recipe.getInputIngredient(), ingMap, fakeGroupMap);
            ingMap.addIngredient(NSSFluid.createFluid(recipe.getInputFluid().getFluid()), recipe.getInputFluid().getAmount());
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
            return recipeType == RegistryEntries.RECIPETYPE_ENVIRONMENTAL_ACCUMULATOR;
        }

        @Override
        protected List<Ingredient> getIngredients(RecipeEnvironmentalAccumulator recipe) {
            return Collections.singletonList(recipe.getInputIngredient());
        }
    }

    @ConversionProvider(MODID)
    public static class ECConversionProvider extends AConversionProvider {
        @Override
        public void convert(CustomConversionBuilder builder) {
            builder.comment("Default conversions for EvilCraft")
                    .before(RegistryEntries.FLUID_BLOOD.getSource(), 1)
                    .before(RegistryEntries.FLUID_BLOOD.getFlowing(), 1);
        }
    }
}
