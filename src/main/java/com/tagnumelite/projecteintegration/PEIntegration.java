package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProjectE Integration Mod
 *
 * @author TagnumElite
 * @version 2.0.0
 */
@Mod(modid = PEIApi.MODID, name = PEIApi.NAME, version = PEIApi.VERSION, dependencies = Reference.DEPENDENCIES, certificateFingerprint = "342c9251777bda1ef9b9f1cb1387c2bd4d06cd78", acceptableRemoteVersions = "*")
public class PEIntegration {
	public static Configuration config;
	private static boolean DISABLE = false;
	private static boolean LOADED = false;
	public static final Logger LOG = LogManager.getLogger(PEIApi.MODID);
	private static boolean MAPPER_ERRORED = false;

	private final List<PEIPlugin> PLUGINS = new ArrayList<>();

	public boolean isLoaded() {
		return LOADED;
	}

	@EventHandler
	public void fingerprintViolation(FMLFingerprintViolationEvent event) {
		LOG.warn("Somebody has been tampering with ProjectE Integration jar!");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LOG.info("Starting Phase: Pre Initialization");
		final long startTime = System.currentTimeMillis();

		config = new Configuration(event.getSuggestedConfigurationFile());

		DISABLE = config.getBoolean("disable", ConfigHelper.CATEGORY_GENERAL, false,
				"Disable the mod outright? Why though?");

		if (DISABLE) {
			LOG.info("Finished Phase: Pre Initialization. Mod Disabled");
			return;
		}

		Set<ASMData> ASM_DATA_SET = event.getAsmData().getAll(RegPEIPlugin.class.getCanonicalName());

		for (ASMData asm_data : ASM_DATA_SET) {
			try {
				Class<?> asmClass = Class.forName(asm_data.getClassName());
				RegPEIPlugin plugin_register = asmClass.getAnnotation(RegPEIPlugin.class);
				if (plugin_register != null && PEIPlugin.class.isAssignableFrom(asmClass)) {
					String modid = plugin_register.modid().toLowerCase();

					if (!config.getBoolean("enable", ConfigHelper.getPluginCategory(modid), true, "Enable the plugin")
							|| !Loader.isModLoaded(modid))
						continue;

					Class<? extends PEIPlugin> asm_instance = asmClass.asSubclass(PEIPlugin.class);
					Constructor<? extends PEIPlugin> plugin = asm_instance.getConstructor(String.class,
							Configuration.class);
					PLUGINS.add(plugin.newInstance(modid, config));
				}
			} catch (Throwable t) {
				LOG.error("Failed to load: {}", asm_data.getClassName(), t);
				//t.printStackTrace();
			}
		}

		if (config.hasChanged())
			config.save();

		final long endTime = System.currentTimeMillis();

		LOG.info("Finished Phase: Pre Initialization. Took {}ms", (endTime - startTime));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (DISABLE)
			return;

		LOG.info("Starting Phase: Post Initialization");
		final long startTime = System.currentTimeMillis();

		for (PEIPlugin plugin : PLUGINS) {
			PEIApi.LOG.debug("Running Plugin for Mod: {}", plugin.modid);
			try {
				plugin.setup();
			} catch (Throwable t) {
				LOG.error("Failed to run Plugin for '{}': {}", plugin.modid, t);
				t.printStackTrace();
			}
		}

		PEIApi.registerEMCObjects();
		LOG.info("Added {} Mappers", PEIApi.getMappers().size());

		if (config.hasChanged())
			config.save();

		final long endTime = System.currentTimeMillis();
		LOG.info("Finished Phase: Post Initialization. Took {}ms", (endTime - startTime));
	}

	@EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event) {
		if (DISABLE || LOADED) // If mod is disabled or has already loaded, then return
			return;

		LOG.info("Starting Phase: Server About To Start");
		final long startTime = System.currentTimeMillis();

		for (PEIMapper mapper : PEIApi.getMappers()) {
			PEIApi.LOG.debug("Running Mapper: {} ({})", mapper.name, mapper);
			try {
				mapper.setup();
			} catch (Throwable t) {
				MAPPER_ERRORED = true;
				LOG.error("Mapper '{}' ({}) Failed to run: {}", mapper.name, mapper, t);
				t.printStackTrace();
			}
		}
		
		LOG.info("Added {} Conversions", PEIApi.mapped_conversions);

		if (config.hasChanged())
			config.save();

		PEIApi.clearCache();
		PLUGINS.clear();

		final long endTime = System.currentTimeMillis();
		LOG.info("Finished Phase: Server About To Start. Took {}ms", (endTime - startTime));
		LOADED = true;
	}
}
