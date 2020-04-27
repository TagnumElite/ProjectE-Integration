/*
 * Copyright (c) 2019-2020 TagnumElite
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.tagnumelite.projecteintegration;

import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.utils.ConfigHelper;
import com.tagnumelite.projecteintegration.api.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

import static com.tagnumelite.projecteintegration.api.PEIApi.*;

/**
 * ProjectE Integration Mod
 *
 * @author TagnumElite
 */
@Mod(modid = MODID, name = NAME, version = VERSION, dependencies = "required-after:projecte;after:*", acceptableRemoteVersions = "*", updateJSON = UPDATE_JSON)
public class PEIntegration {
    public static Configuration config;
    private static boolean DISABLE = false;
    public static final Logger LOG = LogManager.getLogger(MODID);
    private static PEIApi API;
    private static boolean doUpdateCheck = true;
    private static final String updateNotifierPerm = MODID + ".update_notify";
    private static boolean doMapperCheck = true;
    private static final String errorNotifierPerm = MODID + ".error_notify";
    private static ForgeVersion.CheckResult versionCheck;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        DISABLE = config.getBoolean("disable", ConfigHelper.CATEGORY_GENERAL, false,
            "Disable the mod outright? Why download it though?");

        if (DISABLE) return;
        MinecraftForge.EVENT_BUS.register(this);

        doUpdateCheck = config.getBoolean("update_check", ConfigHelper.CATEGORY_GENERAL, true,
            "Display message in chat about any updates");
        doMapperCheck = config.getBoolean("error_check", ConfigHelper.CATEGORY_GENERAL, true,
            "Display message in chat about any failed plugins/mappers");

        API = new PEIApi(config, event.getAsmData());
        if (config.hasChanged()) config.save();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (DISABLE) return;
        API.setupPlugins();
        versionCheck = ForgeVersion.getResult(Loader.instance().activeModContainer());
        if (config.hasChanged()) config.save();
        PermissionAPI.registerNode(updateNotifierPerm, DefaultPermissionLevel.OP, "Should this user recieve update notifications");
        PermissionAPI.registerNode(errorNotifierPerm, DefaultPermissionLevel.OP, "Should this user recieve notifications about failed mappers");
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        // If mod is disabled, then return and skip the rest
        if (DISABLE) return;
        API.setupMappers();
        if (config.hasChanged()) config.save();
    }

    @SubscribeEvent
    //TODO: Make translation files and keys instead of permanent english.
    public void playerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        if (!DISABLE) {
            //player.sendMessage(new TextComponentTranslation("text.altar_created.message"));
            EntityPlayer player = event.player;
            if (doUpdateCheck && PermissionAPI.hasPermission(player, updateNotifierPerm)) {
                LOG.info("PEI Version Status: {}", versionCheck.status);
                if (versionCheck.status == ForgeVersion.Status.OUTDATED) {
                    player.sendMessage(Utils.prefixComponent(new TextComponentString(TextFormatting.RED + "There is a new version out.")));
                } else if (versionCheck.status == ForgeVersion.Status.FAILED) {
                    player.sendMessage(Utils.prefixComponent(new TextComponentString(TextFormatting.RED + "Failed to get updates")));
                }
            }
            if (doMapperCheck && PermissionAPI.hasPermission(player, errorNotifierPerm)) {
                int failedPlugins = API.getFailedPlugins().size();
                if (failedPlugins > 0) {
                    player.sendMessage(Utils.prefixComponent(new TextComponentString("" + failedPlugins + TextFormatting.RED + " plugin(s) that have failed!")));
                }
                int failedMappers = API.getFailedMappers().size();
                if (failedMappers > 0) {
                    player.sendMessage(Utils.prefixComponent(new TextComponentString("" + failedMappers + TextFormatting.RED + " mapper(s) that have failed!")));
                }
            }
        }
    }
}
