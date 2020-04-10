package com.tagnumelite.projecteintegration.api.internal.input;

import net.minecraft.item.crafting.Ingredient;

public class SizedIngredient extends SizedObject<Ingredient> {
	public SizedIngredient(int amount, Ingredient obj) {
		super(amount, obj);
	}
}
