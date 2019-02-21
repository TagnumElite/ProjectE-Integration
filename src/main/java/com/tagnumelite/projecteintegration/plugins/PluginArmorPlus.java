package com.tagnumelite.projecteintegration.plugins;

import com.sofodev.armorplus.api.crafting.IRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseCraftingManager;
import com.sofodev.armorplus.api.crafting.base.BaseShapedOreRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapedRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapelessOreRecipe;
import com.sofodev.armorplus.api.crafting.base.BaseShapelessRecipe;
import com.tagnumelite.projecteintegration.other.Utils;

import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.Configuration;

@Plugin(modid="armorplus")
public class PluginArmorPlus implements IPlugin {
	private static boolean enable_workbench_conversions;

	@Override
	public void addConfig(Configuration config, String category) {
		enable_workbench_conversions = config.getBoolean("enable_workbench_conversions", category, true, "Enable Workbench Conversions");
	}

	@Override
	public void addEMC(IEMCProxy proxy) {}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addConversions(IConversionProxy proxy) {
		if (enable_workbench_conversions) {
			BaseCraftingManager WORK_BENCH = BaseCraftingManager.getWBInstance();
			BaseCraftingManager HIGH_TECH_BENCH = BaseCraftingManager.getUTBInstance();
			BaseCraftingManager ULTIMATE_BENCH = BaseCraftingManager.getHTBInstance();
			BaseCraftingManager CHAMPION_BENCH = BaseCraftingManager.getCBInstance();
			
			BaseCraftingManager[] benches = {WORK_BENCH, HIGH_TECH_BENCH, ULTIMATE_BENCH, CHAMPION_BENCH};
			
			for (BaseCraftingManager bench : benches) {
				for (IRecipe raw : bench.getRecipeList()) {
					ItemStack output = raw.getRecipeOutput();
					
					if (output == null || output.isEmpty())
						continue;
					
					IngredientMap ingredients = new IngredientMap();
					
					boolean oreShaped = raw instanceof BaseShapedOreRecipe;
					boolean oreShapeless = raw instanceof BaseShapelessOreRecipe;
					
					if (oreShaped || oreShapeless) {
						NonNullList<Object> input = NonNullList.create();
						if (oreShaped) {
							for (Object in : ((BaseShapedOreRecipe) raw).getInput()) {
								if (in != null)
									input.add(in);
							}
						} else if (oreShapeless) {
							input = ((BaseShapelessOreRecipe) raw).getInput();
						}
						
						for (Object i : input) {
							if (i instanceof NonNullList<?>) {
								ingredients.addIngredient(Utils.createFromList(proxy, i), 1);
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
					
					proxy.addConversion(output.getCount(), output, ingredients.getMap());
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addIngredientsFromNonNullList(NonNullList<ItemStack> input, IngredientMap ingredientMap ) {
		for (ItemStack item : input) {
			if (item == null || item.isEmpty())
				continue;
			
			ingredientMap.addIngredient(item, item.getCount());
		}
	}

	@Override
	public void addBlacklist(IBlacklistProxy proxy) {}

	@Override
	public void addTransmutation(ITransmutationProxy proxy) {}
}
