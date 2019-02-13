package com.tagnumelite.projecteintegration.plugins;

import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraftforge.common.config.Configuration;

public interface IPlugin {
	public void addConfig(Configuration config, String category);
	public void addEMC(IEMCProxy proxy);
	public void addConversions(IConversionProxy proxy);
}
