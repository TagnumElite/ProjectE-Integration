package com.tagnumelite.projecteintegration;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tagnumelite.projecteintegration.plugins.PluginActuallyAdditions;
import com.tagnumelite.projecteintegration.plugins.PluginAppliedEnergistics;
import com.tagnumelite.projecteintegration.plugins.PluginAvaritia;
import com.tagnumelite.projecteintegration.plugins.PluginExtendedCrafting;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies=Reference.DEPENDENCIES, certificateFingerprint="342c9251777bda1ef9b9f1cb1387c2bd4d06cd78")
public class ProjectEIntegration
{
    public static Logger LOG = LogManager.getLogger(Reference.MODID);
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	LOG.info("Starting Phase: Post Initialization");
    	IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
    	
    	if (Loader.isModLoaded("actuallyadditions")) {
    		PluginActuallyAdditions.addConversions(conversion_proxy);
    	}
    	
    	
    	if (Loader.isModLoaded("extendedcrafting")) {
    		PluginExtendedCrafting.addEMC(emc_proxy);
    		PluginExtendedCrafting.addConversions(conversion_proxy);
    	}
    }
}
