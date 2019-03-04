package com.tagnumelite.projecteintegration.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IBlacklistProxy;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

/**
 * ProjectE Integeration API
 * @author TagnumElite (Tagan Hoyle https://tagnumelite.com)
 */
public class PEIApi {
    public static final String MODID = "projecteintegration";
    public static final String APIID = MODID + "api";
    public static final String NAME = "ProjectE Integration";
    public static final String VERSION = "@VERSION@";
    
	public static final Map<Ingredient, Object> INGREDIENT_CACHE = new HashMap<Ingredient, Object>();
	public static final Map<List<?>, Object> LIST_CACHE = new HashMap<List<?>, Object>();

	public static final IBlacklistProxy blacklist_proxy = ProjectEAPI.getBlacklistProxy();
	public static final IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
	public static final IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
	public static final ITransmutationProxy transmutation_proxy = ProjectEAPI.getTransmutationProxy();

    public static final Logger LOG = LogManager.getLogger(APIID);
    /**
	
    /**
     * @param ingredient {@code Ingredient} The ingredient to Object from
     * @return Returns {@code null} if the Ingredient is empty or null
     */
	public static Object getIngredient(Ingredient ingredient) {
		if (ingredient == null || ingredient == Ingredient.EMPTY)
			return null;
		
		if (INGREDIENT_CACHE.containsKey(ingredient))
			return INGREDIENT_CACHE.get(ingredient);
		
		Object obj = new Object();
		
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			if (stack == null || stack.isEmpty())
				continue;
			
			conversion_proxy.addConversion(1, obj, ImmutableMap.of(stack, 1));
		}
		
		INGREDIENT_CACHE.put(ingredient, obj);
		return obj;
	}

	public static Object getList(List<?> list) {
		if (list == null || list.isEmpty()) {
			LOG.debug("Recieved empty list: {}", list);
			return null; 
		}
		
		if (LIST_CACHE.containsKey(list))
			return LIST_CACHE.get(list);
		
		Object obj = new Object();
		
		for (Object object : list) {
			if (object == null)
				continue;
			
			if (object instanceof Ingredient)
				if (object == Ingredient.EMPTY)
					continue;
				else
					object = getIngredient((Ingredient) object);
			else if (object instanceof ItemStack && ((ItemStack) object).isEmpty())
				continue;
			else if (object instanceof FluidStack && ((FluidStack) object).amount == 0)
				continue;
			
			conversion_proxy.addConversion(1, obj, ImmutableMap.of(object, 1));
		}
		
		LIST_CACHE.put(list, obj);
		return obj;
	}
	
	public static void clearCache() {
		INGREDIENT_CACHE.clear();
		LIST_CACHE.clear();
	}
}
