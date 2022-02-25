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

package com.tagnumelite.projecteintegration.api.recipe;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.Utils;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.EMCMappingHandler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Tuple;

import java.util.*;

@EMCMapper
public class PEIRecipeMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
    private static final Map<ACustomRecipeMapper<?>, String> recipeMappers = new HashMap<>();

    public static void loadMappers() {
        if (recipeMappers.isEmpty()) {
            recipeMappers.putAll(Utils.getCustomRecipeMappers());
        }
    }

    @Override
    public String getName() {
        return "ProjectEIntegrationRecipeMapper";
    }

    @Override
    public String getDescription() {
        return "Recipe mapper for custom recipes that don't implement IRecipe";
    }

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, DataPackRegistries dataPackRegistries, IResourceManager iResourceManager) {
        NSSFake.setCurrentNamespace(PEIntegration.MODID + "RecipeMapper");

        NSSFakeGroupManager fakeGroupManager = new NSSFakeGroupManager();
        for (Map.Entry<ACustomRecipeMapper<?>, String> mapperEntry : recipeMappers.entrySet()) {
            final ACustomRecipeMapper<?> recipeMapper = mapperEntry.getKey();
            final String modid = mapperEntry.getValue();
            final String name = recipeMapper.getName();
            final String configKey = getName() + '.' + name + ".enabled";

            if (EMCMappingHandler.getOrSetDefault(config, configKey, recipeMapper.getDescription(), true)) {
                NSSFakeGroupManager.setNamespace(modid);
                List<?> recipes = recipeMapper.getRecipes();
                for (Object recipe : recipes) {
                    try {
                        if (!recipeMapper.handleRecipe(mapper, recipe, fakeGroupManager)) {
                            PEIntegration.debugLog("Recipe Mapper ({}) failed to handle recipe: {}", name, recipe);
                        }
                    } catch (Exception e) {
                        PEIntegration.LOGGER.error("Custom Recipe Mapper ({}) failed to handle recipe: {}", name, recipe, e);
                    }
                }
            }
        }

        NSSFake.resetNamespace();
        NSSFakeGroupManager.resetNamespace();
    }

    private static class NSSFakeGroupManager implements INSSFakeGroupManager {
        private static String namespace = "";
        private final Map<Set<NormalizedSimpleStack>, NormalizedSimpleStack> groups = new HashMap<>();
        private int fakeIndex;

        public static void setNamespace(String namespace) {
            NSSFakeGroupManager.namespace = namespace;
        }

        public static void resetNamespace() {
            NSSFakeGroupManager.namespace = "";
        }

        @Override
        public Tuple<NormalizedSimpleStack, Boolean> getOrCreateFakeGroup(Set<NormalizedSimpleStack> normalizedSimpleStacks) {
            NormalizedSimpleStack stack = groups.get(normalizedSimpleStacks);
            if (stack == null) {
                stack = NSSFake.create(namespace + "_" + fakeIndex++);
                groups.put(new HashSet<>(normalizedSimpleStacks), stack);
                return new Tuple<>(stack, true);
            }
            return new Tuple<>(stack, false);
        }
    }
}
