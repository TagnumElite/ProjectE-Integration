package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.recipe.PEIRecipeMapper;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PEIntegration.MODID)
public class PEIntegration {
    public static final String MODID = "projecteintegration";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public PEIntegration(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
    }

    public static void debugLog(String msg, Object... args) {
        if (!FMLEnvironment.production || ProjectEConfig.common.debugLogging.get()) {
            LOGGER.info(msg, args);
        } else {
            LOGGER.debug(msg, args);
        }
    }

    public static ResourceLocation RL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PEIRecipeMapper.loadMappers();
    }
}
