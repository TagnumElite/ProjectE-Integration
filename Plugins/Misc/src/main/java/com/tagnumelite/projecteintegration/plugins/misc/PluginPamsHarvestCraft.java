package com.tagnumelite.projecteintegration.plugins.misc;

import com.pam.harvestcraft.item.GrinderRecipes;
import com.pam.harvestcraft.item.PresserRecipes;
import com.pam.harvestcraft.item.WaterFilterRecipes;
import com.pam.harvestcraft.tileentities.MarketData;
import com.pam.harvestcraft.tileentities.MarketItems;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.Map;

@PEIPlugin("harvestcraft")
public class PluginPamsHarvestCraft extends APEIPlugin {
    public PluginPamsHarvestCraft(String modid, Configuration config) {
        super(modid, config);
    }

    @Override
    public void setup() {
        addEMC("listAllfruit", 128);
        addEMC("listAllgrain", 24);
        addEMC("listAllmeatraw", 64);
        addEMC("listAllveggie", 64);
        addEMC("listAllnut", 64);
        addEMC("listAllgreenveggie", 64);
        addEMC("listAllseed", 16);
        addEMC("listAllberry", 16);

        addMapper(new MarketMapper());
        addMapper(new MachineMapper("Grinder", GrinderRecipes.class, "grindingList"));
        addMapper(new MachineMapper("Presser", PresserRecipes.class, "pressingList"));
        addMapper(new MachineMapper("Water Filter", WaterFilterRecipes.class, "waterfilterList"));
    }

    private static class MarketMapper extends PEIMapper {
        protected MarketMapper() {
            super("Market", "Enable mapper for the market?", true);
        }

        @Override
        public void setup() {
            for (int i = 0; i < MarketItems.getSize(); i++) {
                MarketData data = MarketItems.getData(i);
                ItemStack input = data.getCurrency();
                input.setCount(data.getPrice());
                addRecipe(data.getItem(), input);
            }
        }
    }

    private static class MachineMapper extends PEIMapper {
        private final Class<?> machine;
        private final String field;

        public MachineMapper(String name, Class<?> machine, String field) {
            super(name, "This may not work 100% of the time because of hacky methods used to fetch the recipes!");
            this.machine = machine;
            this.field = field;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void setup() {
            try {
                for (Map.Entry<ItemStack, ItemStack[]> entr : ((Map<ItemStack, ItemStack[]>) FieldUtils
                    .readStaticField(machine, field, true)).entrySet()) {
                    ItemStack[] outputs = entr.getValue();
                    ItemStack input = entr.getKey();
                    if (outputs.length == 2) {
                        if (Utils.isSameItem(outputs[0], outputs[1])) {
                            ItemStack output = outputs[0].copy();
                            output.setCount(outputs[0].getCount() + outputs[1].getCount());

                            addRecipe(output, input.copy());
                            continue;
                        }
                    }

                    addRecipe(Utils.createOutputs(outputs), input);
					/*
					for (ItemStack output : outputs) {
						addRecipe(output, input);
					}*/
                }
            } catch (Exception e) {
                PEIApi.LOG.error("Failed to get HarvestCraft machine '{}' recipes: {}", name, e);
            }
        }
    }
}
