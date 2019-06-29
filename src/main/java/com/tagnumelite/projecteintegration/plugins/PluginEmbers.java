/*
 * Copyright (c) 2019 TagnumElite
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
package com.tagnumelite.projecteintegration.plugins;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraftforge.common.config.Configuration;
import teamroots.embers.recipe.AlchemyRecipe;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@RegPEIPlugin(modid = "embers")
public class PluginEmbers extends PEIPlugin {
	public PluginEmbers(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new AlchemyMapper());
		addMapper(new MeltingMapper());
		addMapper(new MixingMapper());
		addMapper(new StamperMapper());
	}

	private class AlchemyMapper extends PEIMapper {
		public AlchemyMapper() {
			super("Alchemy");
		}

		@Override
		public void setup() {
			for (AlchemyRecipe recipe : RecipeRegistry.alchemyRecipes) {
				addRecipe(recipe.result.copy(), recipe.centerInput, recipe.inputs.toArray());
			}
		}
	}

	private class MeltingMapper extends PEIMapper {
		public MeltingMapper() {
			super("Melting");
		}

		@Override
		public void setup() {
			for (ItemMeltingRecipe recipe : RecipeRegistry.meltingRecipes) {
				addRecipe(recipe.getFluid(), recipe.getStack());
			}
		}
	}

	private class MixingMapper extends PEIMapper {
		public MixingMapper() {
			super("Mixing");
		}

		@Override
		public void setup() {
			for (FluidMixingRecipe recipe : RecipeRegistry.mixingRecipes) {
				addRecipe(recipe.output.copy(), recipe.inputs.toArray());
			}
		}
	}

	private class StamperMapper extends PEIMapper {
		public StamperMapper() {
			super("Stamper");
		}

		@Override
		public void setup() {
			for (ItemStampingRecipe recipe : RecipeRegistry.stampingRecipes) {
				addRecipe(recipe.result.copy(), recipe.getStack(), recipe.getFluid());
			}
		}
	}
}
