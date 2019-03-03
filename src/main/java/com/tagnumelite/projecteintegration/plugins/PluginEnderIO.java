package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.HashMap;
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
import crazypants.enderio.base.recipe.soul.BasicSoulBinderRecipe;
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
	
	@Override
	public void setupIntegration() {
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
		private final Map<String, Object> ENTITY_MAP = new HashMap<String, Object>();
		
		public SoulBinderMapper() {
			super("Soul Binder",
				  "");
		}
		
		private Object getObjectFromSoulList(NNList<ResourceLocation> souls) {
			List<Object> mapped_souls = new ArrayList<Object>();
			List<Integer> mapped_emc = new ArrayList<Integer>();
			
			for (ResourceLocation soul: souls) {
				String soul_name = soul.getResourcePath();
				
				PEIApi.LOG.debug("EnderIO Soul Found: {}", soul_name);
				
				if (ENTITY_MAP.containsKey(name)) {
					mapped_souls.add(ENTITY_MAP.get(soul_name));
					continue;
				}
				
				Object soul_obj = new Object();
				final int emc = config.getInt("emc_soul_" + soul_name, category, 0, 0, Integer.MAX_VALUE, "EMC value for this entity's soul");
				mapped_emc.add(emc);
					
				PEIApi.emc_proxy.registerCustomEMC(soul_obj, (long) emc);	
				ENTITY_MAP.put(soul_name, soul_obj);
			}
			boolean non = true;
			for (int emc : mapped_emc) {
				if (emc > 0)
					non = false;
			}
			
			if (non)
				return null; // Return Null when all emc values are 0 or below because the souls haven't been given an emc value
			else			
				return PEIApi.getList(mapped_souls);
		}

		@Override
		public void setup() {
			List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.SOULBINDER).values().stream()
			.filter(r -> r instanceof ISoulBinderRecipe).collect(Collectors.toList());
			
			for (IMachineRecipe machine_recipe : recipes) {
				if (machine_recipe instanceof BasicSoulBinderRecipe) {
					BasicSoulBinderRecipe recipe = (BasicSoulBinderRecipe) machine_recipe;
					
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
