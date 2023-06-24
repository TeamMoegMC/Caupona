/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.util;

import java.util.function.Predicate;

import com.teammoeg.caupona.CPMain;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public enum TabType implements Predicate<ResourceKey<CreativeModeTab>>{
	MAIN(e->e.equals(CPMain.main.getKey())),
	FOODS(e->e.equals(CPMain.foods.getKey())),
	MAIN_AND_TRANSPORTATION(e->e.equals(CPMain.main.getKey())||e.equals(CreativeModeTabs.TOOLS_AND_UTILITIES)),
	HIDDEN(e->false);
	private final Predicate<ResourceKey<CreativeModeTab>> predicate;

	private TabType(Predicate<ResourceKey<CreativeModeTab>> predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(ResourceKey<CreativeModeTab> t) {
		return predicate.test(t);
	}
	
}
