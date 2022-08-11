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
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WolfStatueTile extends CPBaseTile {
	int ticks;
	private int checkTicks;
	boolean isVeryHot;

	public WolfStatueTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.WOLF.get(), pWorldPosition, pBlockState);
		checkTicks = Config.SERVER.wolfTick.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		ticks = nbt.getInt("process");
		isVeryHot = nbt.getBoolean("very_hot");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process", ticks);
		nbt.putBoolean("very_hot", isVeryHot);
	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;

		BlockEntity ste = level.getBlockEntity(this.getBlockPos().below());
		if (ste instanceof IStove) {
			BlockState bs = this.getBlockState();
			int nh = ((IStove) ste).requestHeat();
			int bheat = bs.getValue(WolfStatueBlock.HEAT);
			boolean flag = false;
			bs = bs.setValue(WolfStatueBlock.HEAT, nh);
			if (bheat != nh) {
				flag = true;
			}
			if(!isVeryHot)
				this.setChanged();
			isVeryHot = nh > 0;
			if (isVeryHot && bs.getValue(WolfStatueBlock.WATERLOGGED)) {
				bs = bs.setValue(WolfStatueBlock.WATERLOGGED, false);
				this.level.levelEvent(1501, worldPosition, 0);
				flag = true;
			}
			if (flag)
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs);
			return;
		}
		isVeryHot = false;
		ticks++;
		if (ticks >= checkTicks) {
			ticks = 0;
			BlockPos bp = this.getBlockPos();
			BlockState bs = this.getBlockState();
			int bheat = bs.getValue(WolfStatueBlock.HEAT);
			for (int i = 0; i < 4; i++) {
				bp = bp.below();
				BlockEntity te = this.getLevel().getBlockEntity(bp);
				if (te instanceof BathHeatingTile) {
					int cheat = Math.min(((BathHeatingTile) te).getHeat(), 2);
					
					
					if (cheat != bheat)
						this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs.setValue(WolfStatueBlock.HEAT, cheat));
					return;
				}
			}
			if(bheat!=0)
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), bs.setValue(WolfStatueBlock.HEAT,0));
		}
	}

}
