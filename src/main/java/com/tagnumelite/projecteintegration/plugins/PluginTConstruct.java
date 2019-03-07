package com.tagnumelite.projecteintegration.plugins;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

@RegPEIPlugin(modid="tconstruct")
public class PluginTConstruct extends PEIPlugin {
	public PluginTConstruct(String modid, Configuration config) { super(modid, config); }

	@Override
	public void setup() {
		addMapper(new AlloyMapper());
		addMapper(new DryingMapper());
		addMapper(new MeltingMapper());
	}
	
	private class AlloyMapper extends PEIMapper {
		public AlloyMapper() {
			super("Alloy",
				  "Tinkers Smelter Alloying recipe support");
		}

		@Override
		public void setup() {
			for (AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
				FluidStack output = recipe.getResult();
				if (output == null || output.amount <= 0)
					continue;
				
				List<FluidStack> inputs = recipe.getFluids();
				if (inputs == null || inputs.isEmpty())
					continue;
				
				IngredientMap<Object> ingredients = new IngredientMap<Object>();
				for (FluidStack input : inputs) {
					if (input == null || input.amount <= 0)
						continue;
					
					ingredients.addIngredient(input, input.amount);
				}
				
				Map<Object, Integer> map = ingredients.getMap();
				if (map.isEmpty())
					continue;
				
				addConversion(output, map);
			}
		}
	}
	
	private class DryingMapper extends PEIMapper {
		public DryingMapper() {
			super("Drying",
				  "");
		}

		@Override
		public void setup() {
			for (DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
				ItemStack output = recipe.getResult();
				if (output == null || output.isEmpty())
					continue;
				
				Object input = PEIApi.getList(recipe.input.getInputs());
				if (input == null)
					continue;
				
				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}

	private class MeltingMapper extends PEIMapper {
		public MeltingMapper() {
			super("Melting",
				  "");
		}

		@Override
		public void setup() {
			for (MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
				FluidStack output = recipe.getResult();
				if (output == null || output.amount <= 0)
					continue;
				
				Object input = PEIApi.getList(recipe.input.getInputs());
				if (input == null)
					continue;
				
				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
		
	}
}
