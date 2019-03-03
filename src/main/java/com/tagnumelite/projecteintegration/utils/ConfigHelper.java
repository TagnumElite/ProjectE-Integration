package com.tagnumelite.projecteintegration.utils;

import net.minecraftforge.common.config.Configuration;

public class ConfigHelper {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_PLUGINS = "plugins";
	public static final String CATEGORY_MISC = "misc";

	/**
	 * 
	 * @param modid Modid of the mod
	 * @return String CATEGORY_PLUGINS + CATEGORY_SPLITTER + key
	 */
	public static String getPluginCategory(String modid) {
		return CATEGORY_PLUGINS + Configuration.CATEGORY_SPLITTER + modid;
	}
}
