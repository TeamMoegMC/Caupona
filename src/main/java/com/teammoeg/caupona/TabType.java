package com.teammoeg.caupona;

import java.util.function.Predicate;

import net.minecraft.world.item.CreativeModeTab;

public enum TabType implements Predicate<CreativeModeTab>{
	MAIN(e->e==Main.main),
	FOODS(e->e==Main.foods),
	HIDDEN(e->false);
	private final Predicate<CreativeModeTab> predicate;

	private TabType(Predicate<CreativeModeTab> predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(CreativeModeTab t) {
		return predicate.test(t);
	}
	
}
