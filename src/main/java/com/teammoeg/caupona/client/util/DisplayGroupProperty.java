package com.teammoeg.caupona.client.util;

import com.google.common.collect.ImmutableSet;

import net.neoforged.neoforge.client.model.data.ModelProperty;

public class DisplayGroupProperty extends ModelProperty<ImmutableSet<String>> {
	public static final DisplayGroupProperty PROPERTY=new DisplayGroupProperty();
	private DisplayGroupProperty() {
		super();
	}

}
