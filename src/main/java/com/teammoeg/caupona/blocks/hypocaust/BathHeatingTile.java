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

import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BathHeatingTile extends CPBaseTile {
	private double rate;
	private int val;
	protected int process;//
	private int mp;
	protected int heat;//
	public BathHeatingTile(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		rate=Config.SERVER.bathChance.get();
		val=Config.SERVER.bathExp.get();
		mp=Config.SERVER.bathPath.get();
	}

	public int getHeat() {
		return heat;
	};
	public void setHeat(int val) {
		if(heat>val)return;
		if(val!=0)
			process=mp;
		else
			process=0;
		heat=val;
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process=nbt.getInt("pathTick");
		heat=nbt.getInt("bathHeat");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("pathTick",process);
		nbt.putInt("bathHeat", heat);
	}
	protected static boolean inRange(int pos,double d) {
		return d>=pos&&d<pos+1;
	}
	@Override
	public void tick() {
		int heat=getHeat();
		if(val>0&&heat>0&&this.level.random.nextDouble()<rate&&this.getLevel().getFluidState(this.getBlockPos().above()).is(FluidTags.WATER)){
			int posX=this.getBlockPos().getX();
			int posY=this.getBlockPos().getY();
			int posZ=this.getBlockPos().getZ();
			int addExp=val*heat;
			for(Player p:this.getLevel().players()) {
				if(inRange(posX,p.getX())&&inRange(posZ,p.getZ())&&inRange(posY+1,p.getY())&&p.isInWaterOrBubble()) {
					p.giveExperiencePoints(addExp);
				}
			}
		}
		if(process>0) 
			process--;
		else
			this.heat=0;
		this.syncData();
	}


}
