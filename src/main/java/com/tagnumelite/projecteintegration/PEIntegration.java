package com.tagnumelite.projecteintegration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tagnumelite.projecteintegration.other.ConfigHelper;
import com.tagnumelite.projecteintegration.plugins.IPlugin;
import com.tagnumelite.projecteintegration.plugins.Plugin;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies=Reference.DEPENDENCIES, certificateFingerprint="342c9251777bda1ef9b9f1cb1387c2bd4d06cd78")
public class PEIntegration
{
	public static Configuration config;
	public static boolean DEBUG;
    public static Logger LOG = LogManager.getLogger(Reference.MODID);
    private Map<String, IPlugin> PLUGINS = new HashMap<String, IPlugin>();
    
    @EventHandler
    public void fingerprintViolation(FMLFingerprintViolationEvent event) {
    		LOG.warn("Somebody has been tampering with ProjectE Integration jar!");
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	LOG.info("Starting Phase: Pre Initialization");
    	final long startTime = System.currentTimeMillis();
    	
    	config = new Configuration(event.getSuggestedConfigurationFile());
    	DEBUG = config.getBoolean("debug", ConfigHelper.CATEGORY_GENERAL, false, "Enable DEBUG Logging?");
    	
    	Set<ASMData> ASM_DATA_SET = event.getAsmData().getAll(Plugin.class.getCanonicalName());
    	
    	for (ASMData asm_data : ASM_DATA_SET) {
    		try {
    			Class<?> asmClass = Class.forName(asm_data.getClassName());
    			Plugin plugin = asmClass.getAnnotation(Plugin.class);
    			if (plugin != null && IPlugin.class.isAssignableFrom(asmClass)) {
    				String modid = plugin.modid();
    	    		if (!config.getBoolean("enable", ConfigHelper.getPluginConfig(modid), true, "Enable the plugin") || !Loader.isModLoaded(modid))
    	    			continue;
        			Class<? extends IPlugin> asm_instance = asmClass.asSubclass(IPlugin.class);
        			IPlugin instance = asm_instance.newInstance();
        			PLUGINS.put(modid, instance);
    			}
    		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
    			LOG.error("Failed to load: {}", asm_data.getClassName(), e);
    		}
    	}
    	
    	if (config.hasChanged())
    		config.save();
    	
    	final long endTime = System.currentTimeMillis();
    	
    	LOG.info("Finished Phase: Pre Initialization. Took {}ms", (endTime - startTime));
    }
    
    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
    	LOG.info("Starting Phase: Post Initialization");
    	final long startTime = System.currentTimeMillis();
    	
    	IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
    	IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
    	IBlacklistProxy blacklist_proxy = ProjectEAPI.getBlacklistProxy();
    	ITransmutationProxy transmutation_proxy = ProjectEAPI.getTransmutationProxy();
    	
    	for (Entry<String, IPlugin> entry : PLUGINS.entrySet()) {
    		String category = ConfigHelper.getPluginConfig(entry.getKey());
    		
    		
    		IPlugin plugin = entry.getValue();
    		plugin.addConfig(config, category);
    		
    		plugin.addEMC(emc_proxy);
    		
    		plugin.addConversions(conversion_proxy);
    		
    		plugin.addBlacklist(blacklist_proxy);
    		
    		plugin.addTransmutation(transmutation_proxy);
    	}
    	
    	if(config.hasChanged())
    		config.save();
    	
    	final long endTime = System.currentTimeMillis();
    	
    	LOG.info("Finished Phase: Post Initialization. Took {}ms", (endTime - startTime));
    }
    
    public static void debug(String message) {
    	if (DEBUG)
    		LOG.debug(message);
    }
    
    public static void debug(String message, Object... items) {
    	if (DEBUG)
    		LOG.debug(message, items);
    }
}
