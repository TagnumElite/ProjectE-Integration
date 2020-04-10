package com.tagnumelite.projecteintegration.api.internal.input;

public class SizedObject<T> {
	public final int amount;
	public final T object;
	
	public SizedObject(int amount, T obj) {
		this.amount = amount;
		this.object = obj;
	}
}
