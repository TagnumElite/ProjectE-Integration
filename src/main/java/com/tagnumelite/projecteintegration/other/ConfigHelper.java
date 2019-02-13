package com.tagnumelite.projecteintegration.other;

import net.minecraftforge.common.config.Configuration;

public class ConfigHelper {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_PLUGINS = "plugins";
	public static final String CATEGORY_MISC = "misc";
	
	public static void addPluginConfig () {}

	public static String getPluginConfig(String key) {
		return CATEGORY_PLUGINS + Configuration.CATEGORY_SPLITTER + key;
	}
}
