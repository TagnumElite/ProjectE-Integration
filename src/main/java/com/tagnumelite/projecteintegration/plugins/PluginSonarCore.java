package com.tagnumelite.projecteintegration.plugins;

import java.util.ArrayList;
import java.util.List;
import com.tagnumelite.projecteintegration.api.PEIApi;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import net.minecraft.item.ItemStack;
import sonar.core.recipes.DefaultSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

public class PluginSonarCore {
	public static abstract class SonarMapper extends PEIMapper {
		public SonarMapper(String name, String description) {
			super(name, description);
		}

		protected void addRecipe(DefaultSonarRecipe recipe) {
			List<Object> inputs = new ArrayList<Object>();
			List<List<ItemStack>> inputt = new ArrayList<>();

			for (ISonarRecipeObject input : recipe.inputs()) {
				inputs.add(PEIApi.getList(input.getJEIValue()));
				inputt.add(input.getJEIValue());
			}

			for (ISonarRecipeObject object_output : recipe.outputs()) {
				ItemStack output = object_output.getJEIValue().stream().findFirst().orElse(ItemStack.EMPTY);
				if (output.isEmpty())
					continue;

				addRecipe(output, inputt.stream().toArray());
				
				PEIApi.LOG.debug("Default Sonar Recipe: {} -> {}", inputt, output);
			}
		}
	}
}
