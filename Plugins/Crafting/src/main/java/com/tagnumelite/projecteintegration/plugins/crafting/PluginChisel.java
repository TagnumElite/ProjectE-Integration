/*
 * Copyright (c) 2019-2020 TagnumElite
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

package com.tagnumelite.projecteintegration.plugins.crafting;

import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.api.carving.ICarvingVariation;

import java.util.ArrayList;
import java.util.Objects;

@PEIPlugin("chisel")
public class PluginChisel extends APEIPlugin {
    public PluginChisel(String modid, Configuration config) { super(modid, config); }

    @Override
    public void setup() {
        assert CarvingUtils.getChiselRegistry() != null;
        for (String group : CarvingUtils.getChiselRegistry().getSortedGroupNames()) {
            addMapper(new GroupMapper(group));
        }
    }

    private static class GroupMapper extends PEIMapper {
        private final String group;
        public GroupMapper(String name) {
            super("group_" + name);
            group = name;
        }

        @Override
        public void setup() {
            assert CarvingUtils.getChiselRegistry() != null;
            ICarvingGroup carving_group = Objects.requireNonNull(CarvingUtils.getChiselRegistry().getGroup(group));
            ItemStack primary_item = null;
            ArrayList<ItemStack> variants = new ArrayList<>(carving_group.getVariations().size());

            //TODO: Is this in order, if so we can just use O(n) instead of O2(n)
            for (ICarvingVariation variation : carving_group) {
                if (variation.getOrder() == 0) {
                    primary_item = variation.getStack();
                } else {
                    variants.add(variation.getStack());
                }
            }

            for (ItemStack variation : variants) {
                addRecipe(variation, primary_item);
            }
        }
    }
}
