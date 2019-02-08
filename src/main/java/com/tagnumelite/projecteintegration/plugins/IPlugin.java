package com.tagnumelite.projecteintegration.plugins;

import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;

public interface IPlugin {
	String modid();
	public void addEMC(IEMCProxy proxy);
	public void addConversions(IConversionProxy proxy);
}
