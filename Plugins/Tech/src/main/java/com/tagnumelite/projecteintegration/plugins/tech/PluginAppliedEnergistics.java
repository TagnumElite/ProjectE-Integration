package com.tagnumelite.projecteintegration.plugins.tech;

import appeng.api.AEApi;
import appeng.api.config.CondenserOutput;
import appeng.api.definitions.IMaterials;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.InscriberProcessType;
import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.items.misc.ItemCrystalSeed;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PEIPlugin("appliedenergistics2")
public class PluginAppliedEnergistics extends APEIPlugin {
    @Override
    public void setup() {
        if (AEConfig.instance().isFeatureEnabled(AEFeature.CONDENSER)) addMapper(new CondenserMapper());
        if (AEConfig.instance().isFeatureEnabled(AEFeature.GRIND_STONE)) addMapper(new GrindstoneMapper());
        addMapper(new GrowthMapper());
        if (AEConfig.instance().isFeatureEnabled(AEFeature.INSCRIBER)) addMapper(new InscriberMapper());
    }

    private static class CondenserMapper extends PEIMapper {
        public CondenserMapper() {
            super("Condenser", "Adds condenser mapping for singularity and matter ball.\nUses cobblestone for conversion");
        }

        @Override
        public void setup() {
            ItemStack cobble = new ItemStack(Blocks.COBBLESTONE);
            AEApi.instance().definitions().materials().singularity().maybeStack(1).ifPresent(itemStack -> {
                Map<Object, Integer> map = new HashMap<>();
                map.put(cobble.copy(), CondenserOutput.SINGULARITY.requiredPower);
                addConversion(itemStack, map);
            });
            AEApi.instance().definitions().materials().matterBall().maybeStack(1).ifPresent(itemStack -> {
                Map<Object, Integer> map = new HashMap<>();
                map.put(cobble.copy(), CondenserOutput.MATTER_BALLS.requiredPower);
                addConversion(itemStack, map);
            });
        }
    }

    private static class GrowthMapper extends PEIMapper {
        public GrowthMapper() {
            super("Fluid growing", "Adds seed growing and fluix crafting");
        }

        @Override
        public void setup() {
            IMaterials materials = AEApi.instance().definitions().materials();
            if (AEConfig.instance().isFeatureEnabled(AEFeature.IN_WORLD_FLUIX)) {
                ItemStack charged_certus = materials.certusQuartzCrystalCharged().maybeStack(1).orElse(ItemStack.EMPTY);

                if (!charged_certus.isEmpty()) {
                    materials.fluixCrystal().maybeStack(2).ifPresent(itemStack -> {
                        addRecipe(itemStack, new ItemStack(Items.QUARTZ), new ItemStack(Items.REDSTONE), charged_certus);
                    });
                }
            }
            if (AEConfig.instance().isFeatureEnabled(AEFeature.IN_WORLD_PURIFICATION)) {
                AEApi.instance().definitions().items().crystalSeed().maybeItem().ifPresent(crystal_seed -> {
                    materials.purifiedCertusQuartzCrystal().maybeStack(1).ifPresent(purified_certus -> {
                        addRecipe(purified_certus, new ItemStack(crystal_seed, 1, ItemCrystalSeed.CERTUS));
                    });
                    materials.purifiedNetherQuartzCrystal().maybeStack(1).ifPresent(purified_nether -> {
                        addRecipe(purified_nether, new ItemStack(crystal_seed, 1, ItemCrystalSeed.NETHER));
                    });
                    materials.purifiedFluixCrystal().maybeStack(1).ifPresent(purified_fluix -> {
                        addRecipe(purified_fluix, new ItemStack(crystal_seed, 1, ItemCrystalSeed.FLUIX));
                    });
                });
            }
        }
    }

    private static class InscriberMapper extends PEIMapper {
        public InscriberMapper() {
            super("Inscriber");
        }

        @Override
        public void setup() {
            for (IInscriberRecipe recipe : AEApi.instance().registries().inscriber().getRecipes()) {
                ItemStack output = recipe.getOutput();
                if (output.isEmpty()) continue;

                ItemStack input = recipe.getInputs().get(0);
                if (input == null || input.isEmpty())
                    continue;

                IngredientMap<Object> ingredients = new IngredientMap<>();

                ingredients.addIngredient(input, input.getCount());

                if (recipe.getProcessType() == InscriberProcessType.PRESS) {
                    Optional<ItemStack> input_top = recipe.getTopOptional();
                    if (input_top.isPresent() && input_top.get() != ItemStack.EMPTY)
                        ingredients.addIngredient(input_top.get(), input_top.get().getCount());

                    Optional<ItemStack> input_bottom = recipe.getBottomOptional();
                    if (input_bottom.isPresent() && input_bottom.get() != ItemStack.EMPTY)
                        ingredients.addIngredient(input_bottom.get(), input_bottom.get().getCount());
                }

                addConversion(output, ingredients.getMap());
            }
        }
    }

    private static class GrindstoneMapper extends PEIMapper {
        public GrindstoneMapper() {
            super("Grindstone");
        }

        @Override
        public void setup() {
            for (IGrinderRecipe recipe : AEApi.instance().registries().grinder().getRecipes()) {
                addRecipe(recipe.getOutput(), recipe.getInput());
            }
        }
    }
}
