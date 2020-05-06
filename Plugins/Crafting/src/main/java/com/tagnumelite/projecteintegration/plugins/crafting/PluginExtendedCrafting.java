package com.tagnumelite.projecteintegration.plugins.crafting;

import com.blakebr0.extendedcrafting.crafting.CombinationRecipe;
import com.blakebr0.extendedcrafting.crafting.CombinationRecipeManager;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipe;
import com.blakebr0.extendedcrafting.crafting.CompressorRecipeManager;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

@PEIPlugin("extendedcrafting")
public class PluginExtendedCrafting extends APEIPlugin {
    @Override
    public void setup() {
        addMapper(new ECCompressorMapper());
        addMapper(new ECCombinationMapper());
        addMapper(new ECTableMapper());
        addMapper(new ECEnderTableMapper());
    }

    private static class ECCompressorMapper extends PEIMapper {
        public ECCompressorMapper() {
            super("Compressor");
        }

        @Override
        public void setup() {
            for (CompressorRecipe recipe : CompressorRecipeManager.getInstance().getRecipes()) {
                IngredientMap<Object> ingredients = new IngredientMap<>();
                Object input = recipe.getInput();
                if (input instanceof List) {
                    ingredients.addIngredient(PEIApi.getList((List<?>) input), recipe.getInputCount());
                } else {
                    ingredients.addIngredient(recipe.getInput(), recipe.getInputCount());
                }

                if (recipe.consumeCatalyst()) {
                    ingredients.addIngredient(recipe.getCatalyst(), recipe.getCatalyst().getCount());
                }

                addConversion(recipe.getOutput(), ingredients.getMap());
            }
        }
    }

    private static class ECCombinationMapper extends PEIMapper {
        public ECCombinationMapper() {
            super("Combination");
        }

        @Override
        public void setup() {
            for (CombinationRecipe recipe : CombinationRecipeManager.getInstance().getRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput(), recipe.getPedestalItems().toArray());
            }
        }
    }

    private static class ECTableMapper extends PEIMapper {
        public ECTableMapper() {
            super("Table");
        }

        @Override
        public void setup() {
            for (Object recipe : TableRecipeManager.getInstance().getRecipes()) {
                addRecipe((IRecipe) recipe);
            }
        }
    }

    private static class ECEnderTableMapper extends PEIMapper {
        public ECEnderTableMapper() {
            super("Ender Table");
        }

        @Override
        public void setup() {
            for (Object recipe : EnderCrafterRecipeManager.getInstance().getRecipes()) {
                addRecipe((IRecipe) recipe);
            }
        }
    }
}
