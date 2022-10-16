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

package com.teammoeg.caupona.items;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.RegistryEvents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CPItem extends Item {

	public CPItem(String name, Properties properties) {
		super(properties);
		RegistryEvents.registeredItems.add(Pair.of(new ResourceLocation(Main.MODID, name), this));
	}

}
