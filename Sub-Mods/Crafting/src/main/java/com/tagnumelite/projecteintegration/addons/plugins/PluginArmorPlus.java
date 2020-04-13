package com.tagnumelite.projecteintegration.addons.plugins;

import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;
import com.sofodev.armorplus.api.crafting.base.BaseShapedOreRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapedRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapelessOreRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapelessRecipe;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.List;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin( "armorplus")
public class PluginArmorPlus extends APEIPlugin {
	public PluginArmorPlus(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		addMapper(new BenchMapper(BaseCraftingManager.getWBInstance()));
		addMapper(new BenchMapper(BaseCraftingManager.getUTBInstance()));
		addMapper(new BenchMapper(BaseCraftingManager.getHTBInstance()));
		addMapper(new BenchMapper(BaseCraftingManager.getCBInstance()));
	}

	private static class BenchMapper extends PEIMapper {
		private final BaseCraftingManager bench;

		public BenchMapper(BaseCraftingManager bench) {
			super(bench.getName());
			this.bench = bench;
		}

		@Override
		public void setup() {
			for (IRecipe raw : bench.getRecipeList()) {
				ItemStack output = raw.getRecipeOutput();

				if (output == null || output.isEmpty())
					continue;

				IngredientMap<Object> ingredients = new IngredientMap<Object>();

				boolean oreShaped = raw instanceof BaseShapedOreRecipe;
				boolean oreShapeless = raw instanceof BaseShapelessOreRecipe;

				if (oreShaped || oreShapeless) {
					NonNullList<Object> input = NonNullList.create();
					if (oreShaped) {
						for (Object in : ((BaseShapedOreRecipe) raw).getInput())
							if (in != null)
								input.add(in);

					} else if (oreShapeless) {
						input = ((BaseShapelessOreRecipe) raw).getInput();
					}

					for (Object i : input) {
						if (i instanceof NonNullList<?>) {
							ingredients.addIngredient(PEIApi.getList((List<?>) i), 1);
						} else {
							ingredients.addIngredient(i, 1);
						}
					}
				} else if (raw instanceof BaseShapedRecipe) {
					BaseShapedRecipe recipe = (BaseShapedRecipe) raw;

					NonNullList<ItemStack> input = recipe.getInput();

					addIngredientsFromNonNullList(input, ingredients);
				} else if (raw instanceof BaseShapelessRecipe) {
					BaseShapelessRecipe recipe = (BaseShapelessRecipe) raw;

					NonNullList<ItemStack> input = recipe.getInput();

					addIngredientsFromNonNullList(input, ingredients);
				} else {
					continue;
				}

				addConversion(output, ingredients.getMap());
			}
		}
	}

	private static void addIngredientsFromNonNullList(NonNullList<ItemStack> input,
			IngredientMap<Object> ingredientMap) {
		for (ItemStack item : input) {
			if (item == null || item.isEmpty())
				continue;

			ingredientMap.addIngredient(item, item.getCount());
		}
	}
}
