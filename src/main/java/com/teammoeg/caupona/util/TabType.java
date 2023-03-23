package com.teammoeg.caupona.util;

import java.util.function.Predicate;

import com.teammoeg.caupona.CPMain;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public enum TabType implements Predicate<CreativeModeTab>{
	MAIN(e->e==CPMain.main),
	FOODS(e->e==CPMain.foods),
	MAIN_AND_TRANSPORTATION(e->e==CPMain.main||e==CreativeModeTabs.TOOLS_AND_UTILITIES),
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
