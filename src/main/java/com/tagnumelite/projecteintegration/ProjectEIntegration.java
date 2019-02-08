package com.tagnumelite.projecteintegration;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tagnumelite.projecteintegration.other.PEIConfig;
import com.tagnumelite.projecteintegration.other.Utils;
import com.tagnumelite.projecteintegration.plugins.IPlugin;
import com.tagnumelite.projecteintegration.plugins.Plugin;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies=Reference.DEPENDENCIES, certificateFingerprint="342c9251777bda1ef9b9f1cb1387c2bd4d06cd78")
public class ProjectEIntegration
{
    public static Logger LOG = LogManager.getLogger(Reference.MODID);
    private List<IPlugin> PLUGINS;
    
    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
    		LOG.warn("WARNING: Somebody has been tampering with ProjectE Integration jar!");
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	LOG.info("Starting Phase: Pre Initialization");
    	PEIConfig.load(event.getSuggestedConfigurationFile());
    	
    	PLUGINS = Utils.getPlugins(event.getAsmData(), Plugin.class, IPlugin.class);
    	LOG.info("Finished Phase: Pre Initialization");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	LOG.info("Starting Phase: Post Initialization");
    	IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
    	IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
    	
    	for (IPlugin plugin : PLUGINS) {
    		if (Loader.isModLoaded(plugin.modid())) {
    			plugin.addEMC(emc_proxy);
    			plugin.addConversions(conversion_proxy);
    		}
    	}
    	LOG.info("Finished Phase: Post Initialization");
    }
}
