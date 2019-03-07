package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enderio.core.common.util.NNList;
import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.base.recipe.soul.ISoulBinderRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

@RegPEIPlugin(modid="enderio")
public class PluginEnderIO extends PEIPlugin {
	public PluginEnderIO(String modid, Configuration config) { super(modid, config); }
	private List<IMachineRecipe> SOULBINDER_RECIPES;
	
	@Override
	public void setup() {
		SOULBINDER_RECIPES = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values().stream()
		.filter(r -> r instanceof ISoulBinderRecipe).collect(Collectors.toList());
		
		for (IMachineRecipe sbr : SOULBINDER_RECIPES) {
			if (sbr instanceof ISoulBinderRecipe) {
				for (ResourceLocation resource : ((ISoulBinderRecipe) sbr).getSupportedSouls()) {
					Object soul = PEIApi.getResource(resource);
					if (soul == null) {
						int emc = config.getInt("emc_soul_" + resource.getResourcePath(), category, 0, 0, Integer.MAX_VALUE, "EMC for " + resource.toString());
						PEIApi.addResource(resource, emc);
					}
				}
			}
		}
		
		addMapper(new AlloySmelterMapper());
		addMapper(new SagMillMapper());
		addMapper(new SliceAndSpliceMapper());
		addMapper(new VatMapper());
		addMapper(new SoulBinderMapper());
	}
	
	private IngredientMap<Object> getIngredientsFromNNList(NNList<List<ItemStack>> list) {
		if (list == null || list.isEmpty())
			return null;
		
		IngredientMap<Object> ingredients = new IngredientMap<Object>();
		
		list.forEach(input -> {
			ingredients.addIngredient(PEIApi.getList(input), 1);
		});
		
		return ingredients;
	}
	
	private abstract class IRecipeMapper extends PEIMapper {
		public IRecipeMapper(String name, String description) { super(name, description); }
		
		protected void addRecipe(IRecipe recipe) {
			RecipeOutput[] outputs = recipe.getOutputs();
			if (outputs == null || outputs.length == 0)
				return;
			
			List<ItemStack> items_out = new ArrayList<ItemStack>();
			List<FluidStack> fluids_out = new ArrayList<FluidStack>();
			
			for (RecipeOutput output : outputs) {
				if (output.getChance() < 1F)
					continue;
				
				if (!output.isValid())
					continue;
				
				if (!output.isFluid())
					fluids_out.add(output.getFluidOutput());
				else
					items_out.add(output.getOutput());
			}
			
			recipe.getInputFluidStacks();
			recipe.getInputs();
			
			IngredientMap<Object> ingredients = getIngredientsFromNNList(recipe.getInputStackAlternatives());
			Map<Object, Integer> ing_map = ingredients.getMap();
			if (ingredients == null || ing_map.isEmpty())
				return;
			
			if (items_out.isEmpty() && fluids_out.isEmpty())
				return; //TODO: Log Incorrect Recipe
			
			for (ItemStack item : items_out) {
				addConversion(item, ing_map);
			}
			
			for (FluidStack fluid : fluids_out) {
				addConversion(fluid, ing_map);
			}
		}
	}
	
	private abstract class ManyToOneRecipeMapper extends PEIMapper {
		public ManyToOneRecipeMapper(String name, String description) { super(name, description); }
		
		protected void addRecipe(IManyToOneRecipe recipe) {
			ItemStack output = recipe.getOutput();
			if (output == null || output.isEmpty())
				return;
			
			IngredientMap<Object> ingredients = getIngredientsFromNNList(recipe.getInputStackAlternatives());
			if (ingredients == null || ingredients.getMap().isEmpty())
				return;
			
			addConversion(output, ingredients.getMap());
		}
	}
	
	private class AlloySmelterMapper extends ManyToOneRecipeMapper {
		public AlloySmelterMapper() {
			super("Alloy Smelter",
				  "");
		}

		@Override
		public void setup() {
			for (IManyToOneRecipe recipe : AlloyRecipeManager.getInstance().getRecipes()) {
				addRecipe(recipe);
			}
		}
	}
	
	private class SagMillMapper extends IRecipeMapper {
		public SagMillMapper() {
			super("Sag Mill",
				  "");
		}

		@Override
		public void setup() {
			for (Recipe recipe : SagMillRecipeManager.getInstance().getRecipes()) {
				addRecipe(recipe);
			}
		}
	}
	
	private class SliceAndSpliceMapper extends ManyToOneRecipeMapper {
		public SliceAndSpliceMapper() {
			super("Slice And Splice",
				  "");
		}

		@Override
		public void setup() {
			for (IManyToOneRecipe recipe : SliceAndSpliceRecipeManager.getInstance().getRecipes()) {
				addRecipe(recipe);
			}
		}
	}
	
	private class VatMapper extends IRecipeMapper {
		public VatMapper() {
			super("Vat",
				  "");
		}

		@Override
		public void setup() {
			for (IRecipe recipe : VatRecipeManager.getInstance().getRecipes()) {
				addRecipe(recipe);
			}
		}
	}
	
	private class SoulBinderMapper extends PEIMapper {
		public SoulBinderMapper() {
			super("Soul Binder",
				  "");
		}
		
		private Object getObjectFromSoulList(NNList<ResourceLocation> souls) {
			List<Object> mapped_souls = new ArrayList<Object>();
			
			for (ResourceLocation resource: souls) {
				Object soul = PEIApi.getResource(resource);
				if (soul == null)
					continue;
			}
			
			if (mapped_souls.isEmpty())
				return null;
			else			
				return PEIApi.getList(mapped_souls);
		}

		@Override
		public void setup() {
			for (IMachineRecipe machine_recipe : SOULBINDER_RECIPES) {
				if (machine_recipe instanceof ISoulBinderRecipe) {
					ISoulBinderRecipe recipe = (ISoulBinderRecipe) machine_recipe;
					
					ItemStack output = recipe.getOutputStack();
					if (output == null || output.isEmpty())
						continue;
					
					ItemStack input = recipe.getInputStack();
					if (input == null || input.isEmpty())
						continue;
					
					Object soul_emc = getObjectFromSoulList(recipe.getSupportedSouls());
					if (soul_emc == null)
						addConversion(output, ImmutableMap.of(input, input.getCount()));
					else
						addConversion(output, ImmutableMap.of(input, input.getCount(), soul_emc, 1));
				}
			}
		}
	}
}
