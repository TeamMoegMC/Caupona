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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.items;

import net.minecraft.world.level.block.Block;

import com.teammoeg.caupona.Contents;
import com.teammoeg.caupona.Main;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class CPBlockItem extends BlockItem {
	public CPBlockItem(Block block, Item.Properties props) {
		super(block, props);
	}

	public CPBlockItem(Block block, Item.Properties props, String name) {
		this(block, props.tab(Main.itemGroup));
		this.setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
	}
}
