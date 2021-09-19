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
package com.tagnumelite.projecteintegration.plugins.misc;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@PEIPlugin("securitycraft")
public class PluginSecurityCraft extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new ReinforcedMapper());
    }

    private static class ReinforcedMapper extends PEIMapper {
        public ReinforcedMapper() {
            super("Reinforce", "Enable mapper for reinforced blocks");
        }

        @Override
        public void setup() {
            for (Block block : IReinforcedBlock.BLOCKS) {
                IReinforcedBlock reinforcedBlock = (IReinforcedBlock)block;
                for (Block vanillaBlock : reinforcedBlock.getVanillaBlocks()) {
                    if (reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount()) {
                        addRecipe(new ItemStack(block, 1, reinforcedBlock.getVanillaBlocks().indexOf(vanillaBlock)), new ItemStack(vanillaBlock, 1, reinforcedBlock.getVanillaBlocks().indexOf(vanillaBlock)));
                    } else {
                        for (int i = 0; i < reinforcedBlock.getAmount(); i++) {
                            addRecipe(new ItemStack(block, 1, i), new ItemStack(vanillaBlock, 1, i));
                        }
                    }
                }
            }
        }
    }
}
