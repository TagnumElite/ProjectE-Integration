package com.tagnumelite.projecteintegration.plugins;

import com.google.common.collect.ImmutableMap;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.List;
import java.util.Map;

import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;
import vazkii.botania.api.recipe.RecipeManaInfusion;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.api.recipe.RecipeRuneAltar;

@PEIPlugin("botania")
public class PluginBotania extends APEIPlugin {
	private final Object mana = new Object();
	private final boolean add_emc_to_mana;

	public PluginBotania(String modid, Configuration config) {
		super(modid, config);

		add_emc_to_mana = config.getBoolean("add_emc_to_mana", this.category, true, "Should mana have an EMC?");
	}

	@Override
	public void setup() {
		addEMC("mana", mana, 1, "NOTE: This is effectively a multiplier. Calculation is {EMC VALUE} * {MANA REQUIRED}");

		addMapper(new ElvenTradeMapper());
		addMapper(new PetalMapper());
		addMapper(new PureDaisyMapper());
		addMapper(new ManaInfusionMapper());
		addMapper(new RuneAlterMapper());
	}

	private static Map<Object, Integer> createMapFromList(List<Object> list) {
		if (list.isEmpty())
			return null;

		IngredientMap<Object> map = new IngredientMap<Object>();

		list.forEach(item -> {
			if (item instanceof ItemStack)
				map.addIngredient(item, ((ItemStack) item).getCount());
			else if (item instanceof String)
				map.addIngredient(item, 1);
		});

		return map.getMap();
	}

	private static final ItemStack getItemStackFromBlockState(IBlockState state) {
		return new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
	}

	private class ElvenTradeMapper extends PEIMapper {
		public ElvenTradeMapper() {
			super("Elven Trade");
		}

		@Override
		public void setup() {
			for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) {
				List<ItemStack> outputs = recipe.getOutputs();
				if (outputs.isEmpty())
					continue;

				List<Object> inputs = recipe.getInputs();
				if (inputs.isEmpty())
					continue;

				outputs.forEach(output -> {
					addConversion(output.getCount(), output, createMapFromList(inputs));
				});
			}
		}
	}

	private class PetalMapper extends PEIMapper {
		public PetalMapper() {
			super("Petal");
		}

		@Override
		public void setup() {
			for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;

				List<Object> inputs = recipe.getInputs();
				if (inputs.isEmpty())
					continue;

				addConversion(output.getCount(), output, createMapFromList(inputs));
			}
		}
	}

	private class PureDaisyMapper extends PEIMapper {
		public PureDaisyMapper() {
			super("Pure Daisy");
		}

		@Override
		public void setup() {
			for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
				ItemStack output = getItemStackFromBlockState(recipe.getOutputState());
				if (output == null || output.isEmpty())
					continue;

				Object input = recipe.getInput();
				if (input == null)
					continue;

				if (input instanceof IBlockState) {
					input = getItemStackFromBlockState((IBlockState) input);
					if (input == null) {
						PEIApi.LOG.info("ItemStack is null when fetching from BlockState: {}", input);
						continue;
					}
				} else if (input instanceof Block) {
					if (input instanceof BlockLiquid)
						continue; // TODO: Find a way to get the FluidStack from BlockLiquid
				}

				addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}

	private class ManaInfusionMapper extends PEIMapper {
		public ManaInfusionMapper() {
			super("Mana Infusion");
		}

		@Override
		public void setup() {
			for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;

				Object input = recipe.getInput();
				if (!(input instanceof ItemStack) && !(input instanceof String))
					continue;

				final int manaConsumption = recipe.getManaToConsume();

				if (add_emc_to_mana)
					addConversion(output, ImmutableMap.of(input, 1, mana, manaConsumption));
				else
					addConversion(output, ImmutableMap.of(input, 1));
			}
		}
	}

	private class RuneAlterMapper extends PEIMapper {
		public RuneAlterMapper() {
			super("Rune Alter");
		}

		@Override
		public void setup() {
			for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {
				ItemStack output = recipe.getOutput();
				if (output == null || output.isEmpty())
					continue;

				List<Object> inputs = recipe.getInputs();
				if (inputs == null || inputs.isEmpty())
					continue;

				int manaConsumption = recipe.getManaUsage();

				IngredientMap<Object> ingredients = new IngredientMap<Object>();

				inputs.forEach(input -> {
					if (input instanceof ItemStack)
						ingredients.addIngredient(input, ((ItemStack) input).getCount());
					else
						ingredients.addIngredient(input, 1);
				});

				if (add_emc_to_mana)
					ingredients.addIngredient(mana, manaConsumption);

				addConversion(output, ingredients.getMap());
			}
		}
	}
}
