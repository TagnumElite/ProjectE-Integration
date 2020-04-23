package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.tagnumelite.projecteintegration.api.PEIApi.*;

/**
 * ProjectE Integration Mod
 * @author TagnumElite
 */
@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "required-after:projecte;after:*", acceptableRemoteVersions = "*")
public class PEIntegration {
    public static Configuration config;
    private static boolean DISABLE = false;
    public static final Logger LOG = LogManager.getLogger(MODID);
    private static PEIApi API;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        DISABLE = config.getBoolean("disable", ConfigHelper.CATEGORY_GENERAL, false,
            "Disable the mod outright? Why download it though?");

        if (DISABLE) return;
        API = new PEIApi(config, event.getAsmData());
        if (config.hasChanged()) config.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (DISABLE) return;
        API.setupPlugins();
        if (config.hasChanged()) config.save();
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        // If mod is disabled, then return and skip the rest
        if (DISABLE) return;
        API.setupMappers();
        if (config.hasChanged()) config.save();
    }
}
