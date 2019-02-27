package com.tagnumelite.projecteintegration.api;

import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraftforge.common.config.Configuration;

public interface IPlugin {
	public void addConfig(Configuration config, String category);
	public void addEMC(IEMCProxy proxy);
	public void addConversions(IConversionProxy proxy);
	public void addBlacklist(IBlacklistProxy proxy);
	public void addTransmutation(ITransmutationProxy proxy);
}
