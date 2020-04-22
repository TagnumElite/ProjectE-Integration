package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;
import com.tagnumelite.projecteintegration.api.plugin.APEIPlugin;
import com.tagnumelite.projecteintegration.api.plugin.OnlyIf;
import com.tagnumelite.projecteintegration.api.plugin.PEIPlugin;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tagnumelite.projecteintegration.api.PEIApi.*;

/**
 * ProjectE Integration Mod
 *
 * @author TagnumElite
 * @version 2.0.0
 */
@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "required-after:projecte;after:*", acceptableRemoteVersions = "*")
public class PEIntegration {
    public static Configuration config;
    private static boolean DISABLE = false;
    private static boolean LOADED = false;
    public static final Logger LOG = LogManager.getLogger(MODID);
    private static boolean MAPPER_ERRORED = false;

    private final List<APEIPlugin> PLUGINS = new ArrayList<>();

    public boolean isLoaded() {
        return LOADED;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("Starting Phase: Pre Initialization");
        final long startTime = System.currentTimeMillis();

        config = new Configuration(event.getSuggestedConfigurationFile());

        DISABLE = config.getBoolean("disable", ConfigHelper.CATEGORY_GENERAL, false,
            "Disable the mod outright? Why download it though?");

        if (DISABLE) {
            LOG.info("Finished Phase: Pre Initialization. Mod Disabled");
            return;
        }

        // TODO: Move logic into own class
        event.getAsmData().getAll(PEIPlugin.class.getCanonicalName()).forEach(asm_data -> {
            try {
                Class<?> asmClass = Class.forName(asm_data.getClassName());
                PEIPlugin plugin_register = asmClass.getAnnotation(PEIPlugin.class);
                if (plugin_register != null && APEIPlugin.class.isAssignableFrom(asmClass)) {
                    String modid = plugin_register.value().toLowerCase();

                    if (!Loader.isModLoaded(modid)) return;

                    if (asmClass.isAnnotationPresent(OnlyIf.class)) {
                        OnlyIf onlyIf = asmClass.getAnnotation(OnlyIf.class);
                        if (!StringUtils.isEmpty(onlyIf.versionStartsWith().trim())) {
                            ModContainer modcontainer = Loader.instance().getModList().stream().filter(modContainer -> modContainer.getModId().equals(modid)).collect(Collectors.toList()).get(0);
                            if (!modcontainer.getVersion().startsWith(onlyIf.versionStartsWith())) return;
                            //TODO: Move OnlyIf Logic into own class
                        }
                    }

                    if (!config.getBoolean("enable", ConfigHelper.getPluginCategory(modid), true, "Enable the plugin"))
                        return;

                    Class<? extends APEIPlugin> asm_instance = asmClass.asSubclass(APEIPlugin.class);
                    Constructor<? extends APEIPlugin> plugin = asm_instance.getConstructor(String.class,
                        Configuration.class);
                    PLUGINS.add(plugin.newInstance(modid, config));
                }
            } catch (Throwable t) {
                LOG.error("Failed to load: {}", asm_data.getClassName(), t);
                //t.printStackTrace();
            }
        });

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

        for (APEIPlugin plugin : PLUGINS) {
            PEIApi.LOG.debug("Running Plugin for Mod: {}", plugin.modid);
            try {
                plugin.setup();
            } catch (Throwable t) {
                LOG.error("Failed to run Plugin for '{}': {}", plugin.modid, t);
                t.printStackTrace();
            }
        }

        registerEMCObjects();
        LOG.info("Added {} Mappers", getMappers().size());

        if (config.hasChanged())
            config.save();

        final long endTime = System.currentTimeMillis();
        LOG.info("Finished Phase: Post Initialization. Took {}ms", (endTime - startTime));
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        if (DISABLE || LOADED) // If mod is disabled or has already loaded, then return
            return;

        LOG.info("Starting Phase: Recipe Mapping");
        final long startTime = System.currentTimeMillis();

        for (PEIMapper mapper : getMappers()) {
            PEIApi.LOG.debug("Running Mapper: {} ({})", mapper.name, mapper);
            try {
                mapper.setup();
            } catch (Throwable t) {
                MAPPER_ERRORED = true;
                LOG.error("Mapper '{}' ({}) Failed to run: {}", mapper.name, mapper, t);
                t.printStackTrace();
            }
        }

        LOG.info("Added {} Conversions", mapped_conversions);

        if (config.hasChanged())
            config.save();

        clearCache();
        PLUGINS.clear();

        final long endTime = System.currentTimeMillis();
        LOG.info("Finished Phase: Recipe Mapping. Took {}ms", (endTime - startTime));
        LOADED = true;
    }
}
