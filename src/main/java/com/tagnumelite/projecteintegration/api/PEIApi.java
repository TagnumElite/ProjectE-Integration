package com.tagnumelite.projecteintegration.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tagnumelite.projecteintegration.api.mappers.PEIMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ProjectE Integeration API
 *
 * @author TagnumElite (Tagan Hoyle https://tagnumelite.com)
 */
public class PEIApi {
	public static final String MODID = "projecteintegration";
	public static final String APIID = MODID + "api";
	public static final String NAME = "ProjectE Integration";
	public static final String VERSION = "@VERSION@";

	private static final Map<Ingredient, Object> INGREDIENT_CACHE = new HashMap<Ingredient, Object>();
	private static final Map<List<?>, Object> LIST_CACHE = new HashMap<List<?>, Object>();

	public static final Logger LOG = LogManager.getLogger(APIID);

	// private static final IBlacklistProxy blacklist_proxy =
	// ProjectEAPI.getBlacklistProxy();
	private static final IConversionProxy conversion_proxy = ProjectEAPI.getConversionProxy();
	public static final IEMCProxy emc_proxy = ProjectEAPI.getEMCProxy();
	// private static final ITransmutationProxy transmutation_proxy =
	// ProjectEAPI.getTransmutationProxy();

	private static final Set<PEIMapper> MAPPERS = new HashSet<>();
	private static boolean LOCK_EMC_MAPPER = false;
	private static final Map<Object, Integer> EMC_MAPPERS = new HashMap<>();
	private static final Map<ResourceLocation, Object> RESOURCE_MAP = new HashMap<>();

	public static int mapped_conversions = 0;

	/**
	 * @param mapper
	 *            {@code PEIMapper} The mapper to add
	 */
	public static void addMapper(PEIMapper mapper) {
		MAPPERS.add(mapper);
	}

	public static Set<PEIMapper> getMappers() {
		return ImmutableSet.copyOf(MAPPERS);
	}

	public static void addEMCObject(Object object, int emc) {
		if (LOCK_EMC_MAPPER)
			return;

		EMC_MAPPERS.put(object, emc);
	}

	public static Map<Object, Integer> getEMCObjects() {
		return ImmutableMap.copyOf(EMC_MAPPERS);
	};

	public static void registerEMCObjects() {
		for (Entry<Object, Integer> entry : EMC_MAPPERS.entrySet()) {
			Object obj = entry.getKey();
			if (obj instanceof ItemStack)
				emc_proxy.registerCustomEMC((ItemStack) obj, (long) entry.getValue());
			else
				emc_proxy.registerCustomEMC(obj, (long) entry.getValue());
		}

		EMC_MAPPERS.clear();
		LOCK_EMC_MAPPER = true;
	}

	/**
	 * @param ingredient
	 *            {@code Ingredient} The ingredient to Object from
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
		if (list == null || list.isEmpty())
			return null;

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

	public static Object addResource(ResourceLocation resource, long emc) {
		if (RESOURCE_MAP.containsKey(resource))
			return RESOURCE_MAP.get(resource);

		if (emc <= 0)
			return null;

		Object obj = new Object();
		emc_proxy.registerCustomEMC(obj, emc);
		RESOURCE_MAP.put(resource, obj);

		return obj;
	}

	/**
	 * Gets the resource location from the map, returns null if doesn't exist You
	 * need to {@link addResource} to add the resource to the map
	 *
	 * @param resource
	 *            {@code ResourceLocation} The resource location of the entity
	 * @return The object linked to the resouce
	 */
	public static Object getResource(ResourceLocation resource) {
		return RESOURCE_MAP.get(resource);
	}

	public static void clearCache() {
		INGREDIENT_CACHE.clear();
		LIST_CACHE.clear();
		MAPPERS.clear();
	}
}
