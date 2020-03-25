package com.tagnumelite.projecteintegration.plugins;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tagnumelite.projecteintegration.api.PEIPlugin;
import com.tagnumelite.projecteintegration.api.RegPEIPlugin;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.api.carving.ICarvingVariation;

@RegPEIPlugin(modid="chisel")
public class PluginChisel extends PEIPlugin {
	public PluginChisel(String modid, Configuration config) {
		super(modid, config);
	}

	@Override
	public void setup() {
		for (String group : CarvingUtils.getChiselRegistry().getSortedGroupNames()) {
			addMapper(new GroupMapper(group));
		}
	}
	
	private static class GroupMapper extends PEIMapper {
		public GroupMapper(String name) {
			super(name);
		}

		@Override
		public void setup() {
			ICarvingGroup group = CarvingUtils.getChiselRegistry().getGroup(name);
			
			List<ICarvingVariation> variations = group.getVariations().stream().sorted(Comparator.comparingInt(ICarvingVariation::getOrder)).collect(Collectors.toList());
			ItemStack main_item = variations.get(0).getStack();
			variations.remove(0);
			
			for (ICarvingVariation variation : variations) {
				addRecipe(variation.getStack(), main_item);
			}
		}
	}
}
