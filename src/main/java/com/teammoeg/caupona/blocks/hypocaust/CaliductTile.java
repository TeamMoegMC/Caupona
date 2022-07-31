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

package com.teammoeg.caupona.blocks.hypocaust;

import com.teammoeg.caupona.CPTileTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CaliductTile extends BathHeatingTile {

	public CaliductTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.CALIDUCT.get(), pWorldPosition, pBlockState);

	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;
		super.tick();

	}

}
