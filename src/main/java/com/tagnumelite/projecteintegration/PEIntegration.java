package com.tagnumelite.projecteintegration;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PEIntegration.MODID)
public class PEIntegration {
    public static final String MODID = "projecteintegration";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public PEIntegration() {
    }

    public static void debugLog(String msg, Object... args) {
        if (PECore.DEV_ENVIRONMENT || ProjectEConfig.common.debugLogging.get()) {
            LOGGER.info(msg, args);
        } else {
            LOGGER.debug(msg, args);
        }
    }

}
