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

package com.teammoeg.caupona.blocks;

import java.util.function.BiFunction;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.RegistryEvents;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class CPRegisteredEntityBlock<T extends BlockEntity> extends Block implements CPEntityBlock<T> {
	public final String name;
	private final RegistryObject<BlockEntityType<T>> te;

	public CPRegisteredEntityBlock(String name, Properties blockProps, RegistryObject<BlockEntityType<T>> ste,
			BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(blockProps);
		this.name = name;
		te = ste;
		ResourceLocation registryName = createRegistryName();
		setRegistryName(registryName);

		RegistryEvents.registeredBlocks.add(this);
		if (createItemBlock != null) {
			Item item = createItemBlock.apply(this, new Item.Properties().tab(Main.mainGroup));
			if (item != null) {
				item.setRegistryName(registryName);
				RegistryEvents.registeredItems.add(item);
			}
		}

	}

	public ResourceLocation createRegistryName() {
		return new ResourceLocation(Main.MODID, name);
	}

	@Override
	public RegistryObject<BlockEntityType<T>> getTile() {
		return te;
	}

}
