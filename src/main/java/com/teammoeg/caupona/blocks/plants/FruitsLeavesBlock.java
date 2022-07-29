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

package com.teammoeg.caupona.blocks.plants;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FruitsLeavesBlock extends LeavesBlock implements BonemealableBlock {
	Block fruit;

	public FruitsLeavesBlock(Properties p_54422_, Block f) {
		super(p_54422_);
		this.fruit = f;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (!pState.getValue(PERSISTENT)) {
			if (pState.getValue(DISTANCE) == 7) {
				dropResources(pState, pLevel, pPos);
				pLevel.removeBlock(pPos, false);
			} else {
				if (pRandom.nextInt(51) == 0) {
					if (pLevel.getBlockState(pPos.below()).isAir()) {
						pLevel.setBlockAndUpdate(pPos.below(), fruit.defaultBlockState());
					}
				}
			}
		}
	}

	public boolean isRandomlyTicking(BlockState pState) {
		return !pState.getValue(PERSISTENT);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
		return !pState.getValue(PERSISTENT) && pState.getValue(DISTANCE) != 7;
	}

	@Override
	public boolean isBonemealSuccess(Level pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
		return pRandom.nextInt(4) == 0;
	}

	@Override
	public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
		if (pLevel.getBlockState(pPos.below()).isAir()) {
			pLevel.setBlockAndUpdate(pPos.below(), fruit.defaultBlockState());
		}
	}

}
