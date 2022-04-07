package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.recipe.PEIRecipeMapper;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PEIntegration.MODID)
public class PEIntegration {
    public static final String MODID = "projecteintegration";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public PEIntegration() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    public static void debugLog(String msg, Object... args) {
        if (!FMLEnvironment.production || ProjectEConfig.common.debugLogging.get()) {
            LOGGER.info(msg, args);
        } else {
            LOGGER.debug(msg, args);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PEIRecipeMapper.loadMappers();
    }
}
