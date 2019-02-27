package com.tagnumelite.projecteintegration.plugins;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.PEIntegration;
import com.tagnumelite.projecteintegration.api.IPlugin;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.other.Utils;

import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.InscriberProcessType;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

@PEIPlugin(modid="appliedenergistics2")
public class PluginAppliedEnergistics implements IPlugin {
	private boolean enable_grinder_conversions;
	private boolean enable_inscriber_conversions;
	
	@Override
	public void addConfig(Configuration config, String category) {
		enable_grinder_conversions = config.getBoolean("enable_grinder_conversions", category, true, "Enable Grindstone Recipe Conversions");
		enable_inscriber_conversions = config.getBoolean("enable_inscriber_conversions", category, true, "Enable Inscriber Recipe Conversions");
	}
	
	@Override
	public void addEMC(IEMCProxy proxy) {}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addConversions(IConversionProxy proxy) {
		if (enable_grinder_conversions) {
			for (IGrinderRecipe recipe : AEApi.instance().registries().grinder().getRecipes()) {
				ItemStack output = recipe.getOutput();
				ItemStack input = recipe.getInput();
				if (output.isEmpty() || input.isEmpty())
					continue;
				
				proxy.addConversion(output.getCount(), output, ImmutableMap.of((Object) input, input.getCount()));
				Utils.debugRecipe("Grindstone", output, input);
			}
		}
		
		if (enable_inscriber_conversions) {
			for (IInscriberRecipe recipe : AEApi.instance().registries().inscriber().getRecipes()) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;
				
				ItemStack input = recipe.getInputs().get(0);
				if (input == null || input.isEmpty())
					continue;
				
				IngredientMap ingredients = new IngredientMap();
				
				ingredients.addIngredient(input, input.getCount());
				
				if (recipe.getProcessType() == InscriberProcessType.PRESS) {
					Optional<ItemStack> input_top = recipe.getTopOptional();
					if (input_top.isPresent() && input_top.get() != ItemStack.EMPTY)
						ingredients.addIngredient(input_top.get(), input_top.get().getCount());
					
					Optional<ItemStack> input_bottom = recipe.getBottomOptional();
					if (input_bottom.isPresent() && input_bottom.get() != ItemStack.EMPTY)
						ingredients.addIngredient(input_bottom.get(), input_bottom.get().getCount());
				}
				
				proxy.addConversion(output.getCount(), output, ingredients.getMap());
				Utils.debugRecipe("Inscriber", output, ingredients);
			}
		}
	}

	@Override
	public void addBlacklist(IBlacklistProxy proxy) {}

	@Override
	public void addTransmutation(ITransmutationProxy proxy) {}
}
