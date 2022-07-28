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

package com.teammoeg.caupona.blocks;

import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public interface CPTileBlock<V extends BlockEntity> extends EntityBlock{
	@Override
	public default BlockEntity newBlockEntity(BlockPos p, BlockState s) {
		return getTile().get().create(p, s);
	}
	RegistryObject<BlockEntityType<V>> getTile();
	@Override
	public default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
			BlockEntityType<T> pBlockEntityType) {
		return new BlockEntityTicker<T>() {

			@Override
			public void tick(Level pLevel, BlockPos pPos, BlockState pState, BlockEntity pBlockEntity) {
				if (!pBlockEntity.hasLevel())
					pBlockEntity.setLevel(pLevel);
				if(pBlockEntity instanceof CPBaseTile)
					((CPBaseTile) pBlockEntity).tick();
			}
		};
	}
}
